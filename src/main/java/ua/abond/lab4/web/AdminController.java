package ua.abond.lab4.web;

import org.apache.log4j.Logger;
import ua.abond.lab4.config.core.annotation.Controller;
import ua.abond.lab4.config.core.annotation.Inject;
import ua.abond.lab4.config.core.annotation.RequestMapping;
import ua.abond.lab4.config.core.web.support.Page;
import ua.abond.lab4.config.core.web.support.Pageable;
import ua.abond.lab4.config.core.web.support.RequestMethod;
import ua.abond.lab4.domain.Apartment;
import ua.abond.lab4.domain.Order;
import ua.abond.lab4.domain.Request;
import ua.abond.lab4.service.ApartmentService;
import ua.abond.lab4.service.OrderService;
import ua.abond.lab4.service.RequestService;
import ua.abond.lab4.service.exception.ServiceException;
import ua.abond.lab4.util.OptionalConsumer;
import ua.abond.lab4.web.dto.ConfirmRequestDTO;
import ua.abond.lab4.web.mapper.ConfirmRequestDTORequestMapper;
import ua.abond.lab4.web.mapper.PageableRequestMapper;
import ua.abond.lab4.web.validation.ConfirmRequestDTOValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = Logger.getLogger(AdminController.class);
    private static final String REQUEST_VIEW = "/WEB-INF/pages/admin/request.jsp";
    private static final String REQUESTS_VIEW = "/WEB-INF/pages/admin/requests.jsp";
    private static final String ORDER_VIEW = "/WEB-INF/pages/admin/order.jsp";
    private static final String ORDERS_VIEW = "/WEB-INF/pages/admin/orders.jsp";
    private static final String APARTMENTS_VIEW = "/WEB-INF/pages/admin/apartments.jsp";

    @Inject
    private OrderService orderService;
    @Inject
    private RequestService requestService;
    @Inject
    private ApartmentService apartmentService;

    @RequestMapping(value = "/request/confirm", method = RequestMethod.POST)
    public void confirmRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ConfirmRequestDTO dto = new ConfirmRequestDTORequestMapper().map(req);
        List<String> errors = new ConfirmRequestDTOValidator().validate(dto);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("confirmRequestDTO", dto);
            req.getRequestDispatcher(REQUEST_VIEW).forward(req, resp);
            return;
        }

        try {
            orderService.confirmRequest(dto);
        } catch (ServiceException e) {
            req.setAttribute("errors", errors);
            req.setAttribute("confirmRequestDTO", dto);
            req.getRequestDispatcher(REQUEST_VIEW).forward(req, resp);
            return;
        }
        resp.sendRedirect("/admin/requests");
    }

    @RequestMapping("/request")
    public void getRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        Pageable pageable = new PageableRequestMapper().map(req);
        OptionalConsumer.of(requestService.getById(id)).
                ifPresent(request -> {
                    Page<Apartment> apartmentPage = apartmentService.listMostAppropriate(pageable, request);
                    req.setAttribute("request", request);
                    req.setAttribute("apartments", apartmentPage.getContent());
                    req.setAttribute("pageable", pageable);
                }).
                ifNotPresent(() -> req.setAttribute("errorMessage", "Could not find such order."));
        req.getRequestDispatcher(REQUEST_VIEW).forward(req, resp);
    }

    @RequestMapping("/requests")
    public void getRequests(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Pageable pageable = new PageableRequestMapper().map(req);
        Page<Request> page = requestService.list(pageable);

        req.setAttribute("requests", page.getContent());
        req.setAttribute("pageable", pageable);
        req.getRequestDispatcher(REQUESTS_VIEW).forward(req, resp);
    }

    @RequestMapping("/apartments")
    public void getApartments(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Pageable pageable = new PageableRequestMapper().map(req);
        Page<Apartment> page = apartmentService.list(pageable);

        req.setAttribute("apartments", page.getContent());
        req.setAttribute("pageable", pageable);
        req.setAttribute("count", page.getTotalPages());
        req.getRequestDispatcher(APARTMENTS_VIEW).forward(req, resp);
    }

    @RequestMapping("/orders")
    public void getOrders(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Pageable pageable = new PageableRequestMapper().map(req);
        Page<Order> page = orderService.list(pageable);

        req.setAttribute("orders", page.getContent());
        req.setAttribute("pageable", pageable);
        req.getRequestDispatcher(ORDERS_VIEW).forward(req, resp);
    }
}
