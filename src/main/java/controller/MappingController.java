package controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MappingController {

    default void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }
}
