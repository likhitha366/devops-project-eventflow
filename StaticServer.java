import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

/** Small local server for demonstrating the static EventFlow frontend without Docker. */
public class StaticServer {
  private static final HttpClient HTTP = HttpClient.newHttpClient();

  public static void main(String[] args) throws IOException {
    Path root = Path.of("frontend").toAbsolutePath();
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/", exchange -> {
      if (exchange.getRequestURI().getPath().startsWith("/api/")) proxyApi(exchange);
      else sendFile(exchange, root);
    });
    server.start();
    System.out.println("EventFlow frontend running at http://localhost:8080");
  }

  private static void proxyApi(HttpExchange exchange) throws IOException {
    String path = exchange.getRequestURI().getPath();
    String[][] routes = {
      {"/api/users", "http://localhost:8081/users"},
      {"/api/events", "http://localhost:8082/events"},
      {"/api/registrations", "http://localhost:8083/registrations"}
    };
    for (String[] route : routes) {
      if (path.equals(route[0]) || path.startsWith(route[0] + "/")) {
        String target = route[1] + path.substring(route[0].length());
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null) target += "?" + query;
        byte[] requestBody = exchange.getRequestBody().readAllBytes();
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(target));
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType != null) request.header("Content-Type", contentType);
        HttpRequest.BodyPublisher body = requestBody.length == 0
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofByteArray(requestBody);
        try {
          HttpResponse<byte[]> response = HTTP.send(
              request.method(exchange.getRequestMethod(), body).build(),
              HttpResponse.BodyHandlers.ofByteArray());
          String responseType = response.headers().firstValue("Content-Type").orElse("application/json");
          exchange.getResponseHeaders().set("Content-Type", responseType);
          byte[] responseBody = response.body();
          exchange.sendResponseHeaders(response.statusCode(), responseBody.length == 0 ? -1 : responseBody.length);
          if (responseBody.length > 0) exchange.getResponseBody().write(responseBody);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          sendProxyError(exchange);
        } finally {
          exchange.close();
        }
        return;
      }
    }
    exchange.sendResponseHeaders(404, -1);
    exchange.close();
  }

  private static void sendProxyError(HttpExchange exchange) throws IOException {
    byte[] body = "API service unavailable".getBytes(java.nio.charset.StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(502, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }

  private static void sendFile(HttpExchange exchange, Path root) throws IOException {
    String request = exchange.getRequestURI().getPath();
    Path file = root.resolve(request.equals("/") ? "index.html" : request.substring(1)).normalize();
    if (!file.startsWith(root) || !Files.isRegularFile(file)) { exchange.sendResponseHeaders(404, -1); return; }
    String type = Files.probeContentType(file);
    exchange.getResponseHeaders().set("Content-Type", type == null ? "application/octet-stream" : type);
    byte[] body = Files.readAllBytes(file);
    exchange.sendResponseHeaders(200, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }
}
