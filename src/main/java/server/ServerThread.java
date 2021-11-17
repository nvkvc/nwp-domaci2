package server;

import framework.di.annotations.GET;
import framework.di.annotations.POST;
import framework.di.annotations.Path;
import framework.response.ErrorResponse;
import framework.response.JsonResponse;
import framework.response.Response;
import framework.request.enums.Method;
import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.exceptions.RequestNotValidException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<Object> controllers;
    private Map<PathHTTP, ControllerMethod> routes;

    public ServerThread(Socket socket, Map<PathHTTP, ControllerMethod> routes){
        this.socket = socket;
        this.routes = routes;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }


            // Response example
//            Map<String, Object> responseMap = new HashMap<>();
//            responseMap.put("route_location", request.getLocation());
//            responseMap.put("route_method", request.getMethod().toString());
//            responseMap.put("parameters", request.getParameters());
//            Response response = new JsonResponse(responseMap);


            Response response;

            var request_path = request.getLocation();
            var request_http = request.getMethod().toString();
            PathHTTP pathHTTP = new PathHTTP(request_path, request_http);

            ControllerMethod controllerMethod = this.routes.get(pathHTTP);
            if(controllerMethod != null) {
                response = new JsonResponse(controllerMethod.method.invoke(controllerMethod.controller));
            } else {
                response = new ErrorResponse("This route does not exist!");
            }


            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
