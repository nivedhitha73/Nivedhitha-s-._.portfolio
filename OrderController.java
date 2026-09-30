package com.example.bitebox.controller;
import com.example.bitebox.dao.*;
import com.example.bitebox.model.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Place an order, list recent orders, view one order, cancel, or re-order. */
@WebServlet("/orders")
public class OrderController extends HttpServlet {
    private final OrderDAO orders = new OrderDAO();
    private final MenuDAO menu = new MenuDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");
            if (id != null) {
                Order o = orders.findById(Integer.parseInt(id));
                if (o == null) { res.sendError(404); return; }
                req.setAttribute("order", o);
                req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, res);
            } else {
                req.setAttribute("orders", orders.recent(20));
                req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, res);
            }
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession();
        try {
            switch (req.getParameter("action")) {
                case "place": {
                    Map<Integer, Integer> cart = CartController.cart(s);
                    List<OrderLine> lines = CartController.lines(cart, menu);
                    String name = req.getParameter("customer") == null ? "" : req.getParameter("customer").trim();
                    if (lines.isEmpty() || name.isEmpty()) { s.setAttribute("flash", "Add items and enter your name to place an order."); res.sendRedirect("cart"); return; }
                    Order o = new Order();
                    o.setCustomer(name); o.setPhone(req.getParameter("phone")); o.setNote(req.getParameter("note"));
                    BigDecimal total = BigDecimal.ZERO;
                    for (OrderLine l : lines) { o.getLines().add(l); total = total.add(l.getTotal()); }
                    o.setTotal(total);
                    int id = orders.place(o);
                    cart.clear();
                    s.setAttribute("flash", "Order #" + id + " placed. Thank you, " + name + "!");
                    res.sendRedirect("orders?id=" + id); return;
                }
                case "cancel":
                    orders.cancel(Integer.parseInt(req.getParameter("id")));
                    s.setAttribute("flash", "Order cancelled.");
                    res.sendRedirect("orders?id=" + req.getParameter("id")); return;
                case "reorder": {
                    Order o = orders.findById(Integer.parseInt(req.getParameter("id")));
                    Map<Integer, Integer> cart = CartController.cart(s);
                    if (o != null) for (OrderLine l : o.getLines()) cart.merge(l.getItemId(), l.getQty(), (a, b) -> Math.min(20, a + b));
                    s.setAttribute("flash", "Items from that order are in your cart.");
                    res.sendRedirect("cart"); return;
                }
            }
            res.sendRedirect("orders");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
