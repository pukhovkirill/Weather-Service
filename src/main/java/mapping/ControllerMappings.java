package mapping;

import controller.*;
import org.thymeleaf.web.IWebRequest;

import java.util.HashMap;
import java.util.Map;

public class ControllerMappings {

    private static final Map<String, MappingController> controllerStorage;

    static{
        controllerStorage = new HashMap<>();
        controllerStorage.put("/", new HomeController());
        controllerStorage.put("/login", new LoginController());
        controllerStorage.put("/registration", new RegistrationController());
    }

    public static MappingController resolveControllerForRequest(final IWebRequest request) {
        final String path = getRequestPath(request);
        return controllerStorage.get(path);
    }

    private static String getRequestPath(final IWebRequest request) {

        String requestPath = request.getPathWithinApplication();

        final int fragmentIndex = requestPath.indexOf(';');
        if (fragmentIndex != -1) {
            requestPath = requestPath.substring(0, fragmentIndex);
        }

        return requestPath;

    }
}
