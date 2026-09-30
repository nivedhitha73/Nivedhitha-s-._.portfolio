package com.example.bitebox.controller;
import com.example.bitebox.dao.MenuDAO;
import com.example.bitebox.model.MenuItem;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** /menu = browse + search + category filter. /item = item details (also records "recently viewed"). */
@WebServlet({"/menu", "/item"})
public class MenuController extends HttpServlet {
    private final MenuDAO dao = new MenuDAO();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            LinkedList<Integer> recent = (LinkedList<Integer>) session.getAttribute("recentIds");
            if (recent == null) { recent = new LinkedList<>(); session.setAttribute("recentIds", recent); }

            if ("/item".equals(req.getServletPath())) {
                MenuItem item = dao.findById(Integer.parseInt(req.getParameter("id")));
                if (item == null) { res.sendError(404); return; }
                recent.remove((Integer) item.getId()); recent.addFirst(item.getId());
                while (recent.size() > 5) recent.removeLast();
                req.setAttribute("item", item);
                req.getRequestDispatcher("/WEB-INF/views/item.jsp").forward(req, res);
                return;
            }
            String category = req.getParameter("category"), q = req.getParameter("q");
            List<MenuItem> recentItems = new ArrayList<>();
            for (int id : recent) { MenuItem m = dao.findById(id); if (m != null) recentItems.add(m); }
            req.setAttribute("items", dao.find(category, q));
            req.setAttribute("categories", dao.categories());
            req.setAttribute("selected", category == null ? "" : category);
            req.setAttribute("q", q == null ? "" : q);
            req.setAttribute("recentItems", recentItems);
            req.getRequestDispatcher("/WEB-INF/views/menu.jsp").forward(req, res);
        } catch (Exception e) { throw new ServletException(e); }
    }
}
