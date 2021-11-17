package server;

import framework.di.DiEngine;
import framework.di.annotations.Controller;
import framework.di.annotations.GET;
import framework.di.annotations.POST;
import framework.di.annotations.Path;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws Exception {
        var engine = new DiEngine();
        var instantiatedControllers = engine.getInstantiatedControllers();
        Map<PathHTTP, ControllerMethod> routes = getMethodMap(instantiatedControllers);

        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket, routes)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<PathHTTP, ControllerMethod> getMethodMap(List<Object> controllers) throws Exception {
        Map<PathHTTP, ControllerMethod>  routes = new HashMap<PathHTTP, ControllerMethod> ();
        for (var controller : controllers) {
            var methods = controller.getClass().getDeclaredMethods();
            for (var method: methods) {

                boolean isPostOrGetAnnotationPresent = false;
                if(method.isAnnotationPresent(GET.class) || method.isAnnotationPresent(POST.class)) {
                    isPostOrGetAnnotationPresent = true;
                }
                boolean isPathAnnotationPresent = method.isAnnotationPresent(Path.class);
                if(isPostOrGetAnnotationPresent && !isPathAnnotationPresent) {
                    throw new Exception("In controller " + controller.getClass().getCanonicalName() + " path annotation is missing on method " + method.getName());
                }
                if(isPathAnnotationPresent && !isPostOrGetAnnotationPresent) {
                    throw new Exception("In controller " + controller.getClass().getCanonicalName() + " post/get annotation is missing on method " + method.getName());
                }

                if(isPathAnnotationPresent && isPostOrGetAnnotationPresent) {
                    Path path = method.getAnnotation(Path.class);
                    String method_path = path.value();
                    String method_http = method.isAnnotationPresent(POST.class) ? "POST" : "GET";
                    PathHTTP pathHTTP = new PathHTTP(method_path, method_http);
                    ControllerMethod controllerMethod = routes.get(pathHTTP);
                    if(controllerMethod != null) {
                        throw new Exception("In controller " + controllerMethod.controller.getClass().getCanonicalName() + " a " + method_http + " method is already declared with path: " + method_path);
                    } else {
                        routes.put(pathHTTP, new ControllerMethod(controller, method));
                    }
                }
            }
        }
        return routes;
    }
}
