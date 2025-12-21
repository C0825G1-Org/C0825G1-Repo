package com.codegym.homestay_booking.controller.customer;

import com.codegym.homestay_booking.config.VNPayConfig;
import com.codegym.homestay_booking.entity.Booking;
import com.codegym.homestay_booking.entity.Payment;
import com.codegym.homestay_booking.entity.Payment.PaymentStatus;
import com.codegym.homestay_booking.entity.Room;
import com.codegym.homestay_booking.repository.BookingRepository;
import com.codegym.homestay_booking.repository.PaymentRepository;
import com.codegym.homestay_booking.repository.RoomRepository;
import com.codegym.homestay_booking.service.EmailService;
import com.codegym.homestay_booking.service.InvoicePdfService;
import com.codegym.homestay_booking.service.VNPayService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles customer payment flow for booking
 */
@WebServlet(name = "CustomerPaymentController", urlPatterns = {"/payment/*"})
public class CustomerPaymentController extends HttpServlet {
    
    private VNPayService vnPayService = new VNPayService();
    private PaymentRepository paymentRepository = new PaymentRepository();
    private BookingRepository bookingRepository = new BookingRepository();
    private RoomRepository roomRepository = new RoomRepository();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/customer/booking");
            return;
        }
        
        switch (pathInfo) {
            case "/vnpay-return":
                handleVNPayReturn(request, response);
                break;
            case "/edit-payment":
                handleEditPayment(request, response);
                break;
            case "/success":
                showSuccessPage(request, response);
                break;
            case "/failed":
                showFailedPage(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/customer/booking");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if ("/create".equals(pathInfo)) {
            createPaymentAndRedirect(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/booking");
        }
    }
    
    /**
     * Create payment record and redirect to VNPay
     */
    private void createPaymentAndRedirect(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get form data
            String guestName = request.getParameter("guestName");
            String guestEmail = request.getParameter("guestEmail");
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOut = LocalDate.parse(request.getParameter("checkOutDate"));
            
            // Get room info and calculate price
            Room room = roomRepository.getById(roomId);
            if (room == null) {
                request.getSession().setAttribute("errorMessage", "Phòng không tồn tại!");
                response.sendRedirect(request.getContextPath() + "/rooms");
                return;
            }
            
            // Check room availability
            boolean available = bookingRepository.isRoomAvailable(roomId, checkIn, checkOut);
            if (!available) {
                request.getSession().setAttribute("errorMessage", 
                    "Phòng đã được đặt trong khoảng thời gian này! Vui lòng chọn ngày khác.");
                response.sendRedirect(request.getContextPath() + "/rooms?action=detail&id=" + roomId);
                return;
            }
            
            // Calculate total price
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            float totalPrice = room.getRoomPrice() * nights;
            
            // Generate unique transaction reference
            String txnRef = vnPayService.generateTxnRef();
            
            // Create payment record
            Payment payment = new Payment();
            payment.setTransactionCode(txnRef);
            payment.setVnpTxnRef(txnRef);
            payment.setAmount(totalPrice);
            payment.setGuestName(guestName);
            payment.setGuestEmail(guestEmail);
            payment.setRoomId(roomId);
            payment.setCheckInDate(checkIn);
            payment.setCheckOutDate(checkOut);
            payment.setStatus(PaymentStatus.PENDING);
            
            Payment savedPayment = paymentRepository.insert(payment);
            if (savedPayment == null) {
                request.getSession().setAttribute("errorMessage", "Failed to create payment. Please try again.");
                response.sendRedirect(request.getContextPath() + "/customer/booking");
                return;
            }
            
            // Build return URL
            String baseUrl = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                baseUrl += ":" + request.getServerPort();
            }
            baseUrl += request.getContextPath();
            String returnUrl = VNPayConfig.getReturnUrl(baseUrl);
            
            // Get client IP
            String ipAddress = vnPayService.getIpAddress(request);
            
            // Build order info
            String orderInfo = "Thanh toan booking " + room.getRoomType() + " - " + nights + " dem";
            
            // Create VNPay URL
            String paymentUrl = vnPayService.createPaymentUrl(
                txnRef,
                (long) totalPrice,
                orderInfo,
                ipAddress,
                returnUrl
            );
            
            // Redirect to VNPay
            response.sendRedirect(paymentUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Payment error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/customer/booking");
        }
    }
    
    /**
     * Handle VNPay return (callback after payment)
     */
    private void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Get all params
            Map<String, String> params = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values.length > 0) {
                    params.put(key, values[0]);
                }
            });
            
            String vnpSecureHash = request.getParameter("vnp_SecureHash");
            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            String vnpResponseCode = request.getParameter("vnp_ResponseCode");
            String vnpTransactionNo = request.getParameter("vnp_TransactionNo");
            
            // Verify signature first
            boolean validSignature = vnPayService.verifySignature(params, vnpSecureHash);
            if (!validSignature) {
                request.getSession().setAttribute("errorMessage", "Invalid payment signature!");
                response.sendRedirect(request.getContextPath() + "/payment/failed");
                return;
            }
            
            // Check if this is an edit payment
            Boolean isEditPayment = (Boolean) request.getSession().getAttribute("isEditPayment");
            String editTxnRef = (String) request.getSession().getAttribute("editTxnRef");
            
            if (Boolean.TRUE.equals(isEditPayment) && vnpTxnRef.equals(editTxnRef)) {
                // Handle edit booking payment
                handleEditPaymentReturn(request, response, vnpResponseCode);
                return;
            }
            
            // Normal new booking payment flow
            Payment payment = paymentRepository.getByVnpTxnRef(vnpTxnRef);
            if (payment == null) {
                request.getSession().setAttribute("errorMessage", "Payment not found!");
                response.sendRedirect(request.getContextPath() + "/payment/failed");
                return;
            }
            
            // Check response code (00 = success)
            if ("00".equals(vnpResponseCode)) {
                // Payment successful - CREATE BOOKING NOW
                Booking booking = new Booking();
                booking.setGuestName(payment.getGuestName());
                booking.setGuestEmail(payment.getGuestEmail());
                booking.setRoomId(payment.getRoomId());
                booking.setCheckInDate(payment.getCheckInDate());
                booking.setCheckOutDate(payment.getCheckOutDate());
                booking.setTotalPrice(payment.getAmount());
                booking.setStatus(Booking.BookingStatus.CONFIRMED);  // Direct to CONFIRMED!
                
                boolean inserted = bookingRepository.insert(booking);
                
                if (inserted) {
                    // Update payment with booking ID
                    paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.SUCCESS,
                        vnpResponseCode, vnpTransactionNo, booking.getBookingId());
                    
                    // Send invoice email (async - don't block booking confirmation)
                    try {
                        Room room = roomRepository.getById(booking.getRoomId());
                        java.io.File pdfFile = InvoicePdfService.getInstance().generateInvoicePdf(booking, room);
                        EmailService.getInstance().sendInvoiceEmail(
                            booking.getGuestEmail(),
                            booking.getGuestName(),
                            booking,
                            pdfFile
                        );
                    } catch (Exception emailEx) {
                        // Email failure should NOT affect booking - just log it
                        System.err.println("Failed to send invoice email: " + emailEx.getMessage());
                        emailEx.printStackTrace();
                    }
                    
                    request.getSession().setAttribute("successMessage", 
                        "Thanh toán thành công! Booking #" + booking.getBookingId() + " đã được xác nhận. Hóa đơn đã được gửi qua email.");
                    request.getSession().setAttribute("lastBookingId", booking.getBookingId());
                    response.sendRedirect(request.getContextPath() + "/payment/success");
                } else {
                    // Failed to create booking
                    paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED,
                        vnpResponseCode, vnpTransactionNo, null);
                    request.getSession().setAttribute("errorMessage", "Failed to create booking after payment.");
                    response.sendRedirect(request.getContextPath() + "/payment/failed");
                }
            } else {
                // Payment failed
                paymentRepository.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED,
                    vnpResponseCode, vnpTransactionNo, null);
                request.getSession().setAttribute("errorMessage", 
                    "Thanh toán không thành công. Mã lỗi: " + vnpResponseCode);
                response.sendRedirect(request.getContextPath() + "/payment/failed");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Payment processing error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/payment/failed");
        }
    }
    
    /**
     * Handle edit payment return - update existing booking
     */
    private void handleEditPaymentReturn(HttpServletRequest request, HttpServletResponse response,
            String vnpResponseCode) throws IOException {
        
        // Get edit info from session
        Integer bookingId = (Integer) request.getSession().getAttribute("editBookingId");
        String checkInStr = (String) request.getSession().getAttribute("editCheckIn");
        String checkOutStr = (String) request.getSession().getAttribute("editCheckOut");
        Float newPrice = (Float) request.getSession().getAttribute("editNewPrice");
        String email = (String) request.getSession().getAttribute("editEmail");
        
        // Clear session
        request.getSession().removeAttribute("isEditPayment");
        request.getSession().removeAttribute("editTxnRef");
        request.getSession().removeAttribute("editBookingId");
        request.getSession().removeAttribute("editCheckIn");
        request.getSession().removeAttribute("editCheckOut");
        request.getSession().removeAttribute("editNewPrice");
        request.getSession().removeAttribute("editEmail");
        
        if ("00".equals(vnpResponseCode)) {
            // Payment successful - update booking
            try {
                LocalDate checkIn = LocalDate.parse(checkInStr);
                LocalDate checkOut = LocalDate.parse(checkOutStr);
                
                Booking booking = bookingRepository.getById(bookingId);
                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkOut);
                booking.setTotalPrice(newPrice);
                
                boolean updated = bookingRepository.update(booking);
                
                if (updated) {
                    request.getSession().setAttribute("successMessage", 
                        "Thanh toán bổ sung thành công! Booking #" + bookingId + " đã được cập nhật.");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to update booking.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Error updating booking: " + e.getMessage());
            }
        } else {
            request.getSession().setAttribute("errorMessage", 
                "Thanh toán không thành công. Booking chưa được cập nhật.");
        }
        
        response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
    }
    
    private void showSuccessPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Payment Success");
        request.getRequestDispatcher("/WEB-INF/views/customer/payment-success.jsp").forward(request, response);
    }
    
    private void showFailedPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", "Payment Failed");
        request.getRequestDispatcher("/WEB-INF/views/customer/payment-failed.jsp").forward(request, response);
    }
    
    /**
     * Handle additional payment for booking edit
     */
    private void handleEditPayment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int amount = Integer.parseInt(request.getParameter("amount"));
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            
            // Get edit info from session
            String checkIn = (String) request.getSession().getAttribute("editCheckIn");
            String checkOut = (String) request.getSession().getAttribute("editCheckOut");
            String email = (String) request.getSession().getAttribute("editEmail");
            
            if (checkIn == null || checkOut == null) {
                request.getSession().setAttribute("errorMessage", "Session expired. Please try again.");
                response.sendRedirect(request.getContextPath() + "/my-bookings?email=" + email);
                return;
            }
            
            // Generate unique transaction reference
            String txnRef = "EDIT" + System.currentTimeMillis();
            
            // Store edit flag in session for VNPay return
            request.getSession().setAttribute("isEditPayment", true);
            request.getSession().setAttribute("editTxnRef", txnRef);
            
            // Build return URL
            String baseUrl = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                baseUrl += ":" + request.getServerPort();
            }
            baseUrl += request.getContextPath();
            String returnUrl = baseUrl + "/payment/vnpay-return";
            
            // Get client IP
            String ipAddress = vnPayService.getIpAddress(request);
            
            // Build order info
            String orderInfo = "Thanh toan bo sung booking " + bookingId;
            
            // Create VNPay URL for price difference
            String paymentUrl = vnPayService.createPaymentUrl(
                txnRef,
                amount,
                orderInfo,
                ipAddress,
                returnUrl
            );
            
            // Redirect to VNPay
            response.sendRedirect(paymentUrl);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Payment error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/my-bookings");
        }
    }
}
