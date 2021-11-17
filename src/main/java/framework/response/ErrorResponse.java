package framework.response;

import com.google.gson.Gson;

public class ErrorResponse extends Response{

    private Gson gson;
    public Object jsonObject;

    public ErrorResponse(Object jsonObject)
    {
        this.gson = new Gson();
        this.jsonObject = jsonObject;
    }

    @Override
    public String render() {
        StringBuilder responseContent = new StringBuilder();

        responseContent.append("HTTP/1.1 400 Bad Request\n");
        for (String key : this.header.getKeys()) {
            responseContent.append(key).append(":").append(this.header.get(key)).append("\n");
        }
        responseContent.append("\n");

        responseContent.append(this.gson.toJson(this.jsonObject));

        return responseContent.toString();
    }
}
