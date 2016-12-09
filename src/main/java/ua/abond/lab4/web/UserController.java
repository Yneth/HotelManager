package ua.abond.lab4.web;

import ua.abond.lab4.config.core.annotation.Inject;
import ua.abond.lab4.config.core.web.annotation.Controller;
import ua.abond.lab4.config.core.web.annotation.OnException;
import ua.abond.lab4.config.core.web.annotation.RequestMapping;
import ua.abond.lab4.config.core.web.support.Page;
import ua.abond.lab4.config.core.web.support.Pageable;
import ua.abond.lab4.config.core.web.support.RequestMethod;
import ua.abond.lab4.dao.ApartmentTypeDAO;
import ua.abond.lab4.domain.ApartmentType;
import ua.abond.lab4.domain.Order;
import ua.abond.lab4.domain.Request;
import ua.abond.lab4.domain.User;
import ua.abond.lab4.service.OrderService;
import ua.abond.lab4.service.RequestService;
import ua.abond.lab4.service.UserService;
import ua.abond.lab4.util.Parse;
import ua.abond.lab4.web.mapper.ApartmentRequestRequestMapper;
import ua.abond.lab4.web.mapper.PageableRequestMapper;
import ua.abond.lab4.web.mapper.UserSessionRequestMapper;
import ua.abond.lab4.web.validation.RequestValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    public static final String ORDER_VIEW = "/WEB-INF/pages/user/order.jsp";
    public static final String ORDERS_VIEW = "/WEB-INF/pages/user/orders.jsp";
    public static final String REQUEST_VIEW = "/WEB-INF/pages/user/request.jsp";
    public static final String REQUESTS_VIEW = "/WEB-INF/pages/user/requests.jsp";
    public static final String REQUEST_CREATE_VIEW = "/WEB-INF/pages/user/create-request.jsp";

    @Inject
    private UserService userService;
    @Inject
    private RequestService requestService;
    @Inject
    private OrderService orderService;
    @Inject
    private ApartmentTypeDAO apartmentTypeDAO;

    @RequestMapping("/requests")
    public void viewRequests(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        User user = new UserSessionRequestMapper().map(req);
        Pageable pageable = new PageableRequestMapper().map(req);

        Page<Request> userRequests = requestService.getUserRequests(pageable, user.getId());
        req.setAttribute("requests", userRequests.getContent());
        req.setAttribute("pageable", pageable);

        req.getRequestDispatcher(REQUESTS_VIEW).forward(req, resp);
    }

    @RequestMapping("/request")
    public void viewRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        Long id = Parse.longValue(req.getParameter("id"));
        Request request = requestService.getById(id);
        req.setAttribute("request", request);
        req.getRequestDispatcher(REQUEST_VIEW).forward(req, resp);
    }

    @RequestMapping("/request/new")
    public void getCreateRequestPage(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        List<ApartmentType> list = apartmentTypeDAO.list();
        req.setAttribute("apartmentTypes", list);
        req.getRequestDispatcher(REQUEST_CREATE_VIEW).forward(req, resp);
    }

    @OnException("/user/request/new")
    @RequestMapping(value = "/request/new", method = RequestMethod.POST)
    public void createRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        User user = new UserSessionRequestMapper().map(req);

        Request request = new ApartmentRequestRequestMapper().map(req);
        List<String> errors = new RequestValidator().validate(request);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            getCreateRequestPage(req, resp);
            return;
        }
        request.setUser(user);
        requestService.createRequest(request);
        resp.sendRedirect("/user/requests");
    }

    @OnException(value = "/user/request")
    @RequestMapping(value = "/request/reject", method = RequestMethod.POST)
    public void rejectRequest(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        Long id = Parse.longValue(req.getParameter("id"));
        String comment = req.getParameter("comment");
        requestService.rejectRequest(id, comment);
        resp.sendRedirect("/user/requests");
    }

    @RequestMapping("/orders")
    public void viewOrders(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        User user = new UserSessionRequestMapper().map(req);
        Pageable pageable = new PageableRequestMapper().map(req);

        Page<Order> page = orderService.getUserOrders(pageable, user.getId());
        req.setAttribute("orders", page.getContent());
        req.setAttribute("pageable", pageable);

        req.getRequestDispatcher(ORDERS_VIEW).forward(req, resp);
    }

    @RequestMapping("/order")
    public void viewOrder(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        Long id = Parse.longValue(req.getParameter("id"));
        Order order = orderService.getById(id);
        req.setAttribute("order", order);
        req.getRequestDispatcher(ORDER_VIEW).forward(req, resp);
    }

    @OnException(value = "/user/order")
    @RequestMapping(value = "/order/pay", method = RequestMethod.POST)
    public void payOrder(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        Long id = Parse.longValue(req.getParameter("id"));
        orderService.payOrder(id);
        resp.sendRedirect("/user/orders");
    }
}
