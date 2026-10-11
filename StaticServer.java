import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

/** Small local server for demonstrating the static EventFlow frontend without Docker. */
public class StaticServer {
  public static void main(String[] args) throws IOException {
    Path root = Path.of("frontend").toAbsolutePath();
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/", exchange -> sendFile(exchange, root));
    server.start();
    System.out.println("EventFlow frontend running at http://localhost:8080");
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
