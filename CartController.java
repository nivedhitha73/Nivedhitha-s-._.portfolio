package com.example.bitebox.controller;
import com.example.bitebox.dao.MenuDAO;
import com.example.bitebox.model.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** The cart lives in the HTTP session as {itemId -> quantity}. Prices always come from the database. */
@WebServlet("/cart")
public class CartController extends HttpServlet {
    private final MenuDAO menu = new MenuDAO();

    @SuppressWarnings("unchecked")
    static Map<Integer, Integer> cart(HttpSession s) {
        Map<Integer, Integer> c = (Map<Integer, Integer>) s.getAttribute("cart");
        if (c == null) { c = new LinkedHashMap<>(); s.setAttribute("cart", c); }
        return c;
    }

    static List<OrderLine> lines(Map<Integer, Integer> cart, MenuDAO menu) throws Exception {
        List<OrderLine> out = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            MenuItem m = menu.findById(e.getKey());
            if (m != null) out.add(new OrderLine(m.getId(), m.getName(), m.getPrice(), e.getValue()));
        }
        return out;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            List<OrderLine> lines = lines(cart(req.getSession()), menu);
            BigDecimal total = BigDecimal.ZERO;
            for (OrderLine l : lines) total = total.add(l.getTotal());
            req.setAttribute("lines", lines); req.setAttribute("total", total);
            req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession s = req.getSession();
        Map<Integer, Integer> cart = cart(s);
        String action = req.getParameter("action");
        try {
            int id = req.getParameter("id") == null ? 0 : Integer.parseInt(req.getParameter("id"));
            int qty = req.getParameter("qty") == null ? 1 : Math.max(0, Math.min(20, Integer.parseInt(req.getParameter("qty"))));
            switch (action) {
                case "add":
                    MenuItem m = menu.findById(id);
                    if (m == null) { res.sendError(404); return; }
                    cart.merge(id, Math.max(1, qty), (a, b) -> Math.min(20, a + b));
                    s.setAttribute("flash", m.getName() + " added to your cart.");
                    res.sendRedirect("menu"); return;
                case "set": if (qty == 0) cart.remove(id); else cart.put(id, qty); break;
                case "remove": cart.remove(id); break;
                case "clear": cart.clear(); break;
            }
            res.sendRedirect("cart");
        } catch (Exception e) { throw new ServletException(e); }
    }
}
