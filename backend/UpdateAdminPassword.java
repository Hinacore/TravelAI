import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UpdateAdminPassword {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("admin123");
        System.out.println("Encoded password: " + encodedPassword);
        
        String url = "jdbc:mysql://localhost:3306/travelai?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "1234";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE users SET password = ? WHERE username = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, encodedPassword);
                int rows = stmt.executeUpdate();
                System.out.println("Updated " + rows + " row(s)");
            }
        }
    }
}
