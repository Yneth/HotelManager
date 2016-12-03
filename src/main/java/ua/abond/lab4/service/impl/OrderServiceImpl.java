package ua.abond.lab4.service.impl;

import org.apache.log4j.Logger;
import ua.abond.lab4.config.core.annotation.Component;
import ua.abond.lab4.config.core.annotation.Inject;
import ua.abond.lab4.config.core.web.support.Page;
import ua.abond.lab4.config.core.web.support.Pageable;
import ua.abond.lab4.dao.OrderDAO;
import ua.abond.lab4.domain.Order;
import ua.abond.lab4.service.OrderService;
import ua.abond.lab4.service.exception.ServiceException;
import ua.abond.lab4.web.dto.ConfirmRequestDTO;

import java.util.Optional;

@Component
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);

    private final OrderDAO orderDAO;

    @Inject
    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public void deleteOrder(Order order) {
        orderDAO.deleteById(order.getId());
    }

    @Override
    public Order createOrder(ConfirmRequestDTO requestDTO) {
        Order order = new Order.Builder().buildFrom(requestDTO);
        orderDAO.create(order);
        return order;
    }

    @Override
    public Page<Order> list(Pageable pageable) {
        return orderDAO.list(pageable);
    }

    @Override
    public Optional<Order> getById(Long id) {
        return orderDAO.getById(id);
    }

    @Override
    public void payOrder(Long id) throws ServiceException {
        Order order = orderDAO.getById(id).orElse(null);
        if (order == null) {
            throw new ServiceException("Could not find such order");
        }
        if (order.isPayed()) {
            throw new ServiceException("Cannot pay already payed order.");
        }
        order.setPayed(true);
        orderDAO.update(order);
    }

    @Override
    public Page<Order> getUserOrders(Pageable pageable, Long id) {
        return orderDAO.getUserOrders(pageable, id);
    }
}
