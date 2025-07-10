package com.equal_stage_platform.dev.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PasswordResetFlowStandalone {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String baseUrl = "http://localhost:8080/api/auth";
        String email = "resetuser@example.com";
        String oldPassword = "Password!1234";
        String newPassword = "NewPassword!5678";
        String newPassword2 = "AnotherPassword!91011";

        System.out.println("Registering user for password reset flow...");
        System.out.println("Email: " + email);
        System.out.println("Old Password: " + oldPassword);
        System.out.println("New Password: " + newPassword);
        System.out.println("New Password 2: " + newPassword2);

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/register");
        // 1. Register user
        HttpRequest registerReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\", \"password\":\"" + oldPassword + "\"}"))
            .build();
        HttpResponse<String> registerResp = client.send(registerReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + registerResp.statusCode() +
            "\n\tResponse body: " + registerResp.body());
        if (registerResp.statusCode() != 201) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Register failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/forgot-pass");
        System.out.println("Calling forgot-pass with email: " + email);
        // 2. Forgot password
        HttpRequest forgotReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/forgot-pass"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\"}"))
            .build();
        HttpResponse<String> forgotResp = client.send(forgotReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + forgotResp.statusCode() +
            "\n\tResponse body: " + forgotResp.body());
        if (forgotResp.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Forgot password failed!");
            System.exit(1);
        }
        System.out.println("Please check your email for the reset token.");

        // 3. Prompt for token
        System.out.println("Paste the reset token from Ethereal Email:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String resetToken = reader.readLine();
        System.out.println("Got reset token: " + resetToken);

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/reset-pass-token\n\tTrying to reset password with old password -> should fail");
        // 4. Reset password with token
        HttpRequest resetTokenReq1 = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/reset-pass-token"))
            .header("Content-Type", "application/json")
            .header("token", resetToken)
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"pass\":\"" + oldPassword + "\"}"))
            .build();
        HttpResponse<String> resetTokenResp1 = client.send(resetTokenReq1, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + resetTokenResp1.statusCode() +
            "\n\tResponse body: " + resetTokenResp1.body());
        if (resetTokenResp1.statusCode() != 403) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Reset password with token failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/reset-pass-token");
        // 4. Reset password with token
        HttpRequest resetTokenReq2 = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/reset-pass-token"))
            .header("Content-Type", "application/json")
            .header("token", resetToken)
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"pass\":\"" + newPassword + "\"}"))
            .build();
        HttpResponse<String> resetTokenResp2 = client.send(resetTokenReq2, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + resetTokenResp2.statusCode() +
            "\n\tResponse body: " + resetTokenResp2.body());
        if (resetTokenResp2.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Reset password with token failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/login\n\tLogging in with new password: " + newPassword);
        // 5. Log in with new password
        HttpRequest loginReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\", \"password\":\"" + newPassword + "\"}"))
            .build();
        HttpResponse<String> loginResp = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        String loginRespBody = loginResp.body();
        System.out.println(
            "\tResponse status code: " + loginResp.statusCode() +
            "\n\tResponse body: " + loginRespBody);
        if (loginResp.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Login with new password failed!");
            System.exit(1);
        }
        String jwtToken = extractJsonValue(loginRespBody, "token");
        String refreshToken = extractJsonValue(loginRespBody, "refresh");

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/reset-pass\n\tChanging password back to: " + oldPassword + "->(should fail)...");
        // 6. Change password again
        HttpRequest changePassReq1 = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/reset-pass"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + jwtToken)
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"oldPassword\":\"" + newPassword + "\", \"newPassword\":\"" + oldPassword + "\"}"))
            .build();
        HttpResponse<String> changePassResp1 = client.send(changePassReq1, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + changePassResp1.statusCode() +
            "\n\tResponse body: " + changePassResp1.body());
        if (changePassResp1.statusCode() != 403) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Change password again failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/reset-pass\n\tChanging password again to: " + newPassword2);
        // 6. Change password again
        HttpRequest changePassReq2 = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/reset-pass"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + jwtToken)
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"oldPassword\":\"" + newPassword + "\", \"newPassword\":\"" + newPassword2 + "\"}"))
            .build();
        HttpResponse<String> changePassResp2 = client.send(changePassReq2, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + changePassResp2.statusCode() +
            "\n\tResponse body: " + changePassResp2.body());
        if (changePassResp2.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Change password again failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/logout");
        // 7. Logout
        HttpRequest logoutReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/logout"))
            .header("Authorization", "Bearer " + jwtToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"refresh\":\"" + refreshToken + "\"}"))
            .build();
        HttpResponse<String> logoutResp = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + logoutResp.statusCode() +
            "\n\tResponse body: " + logoutResp.body());
        if (logoutResp.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Logout failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("try to delete account after logout");
        // 8. Try to delete account after logout (should fail)
        HttpRequest deleteAccountReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/delete-account"))
            .header("Authorization", "Bearer " + jwtToken)
            .DELETE()
            .build();
        HttpResponse<String> deleteAccountResp = client.send(deleteAccountReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + deleteAccountResp.statusCode() +
            "\n\tResponse body: " + deleteAccountResp.body());
        if (deleteAccountResp.statusCode() != 401) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Delete account after logout should have failed with 401!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/login\n\tTrying to log in with old password: "+ oldPassword +" -->(should fail)...");
        // 9. Try to log in with old password (should fail)
        HttpRequest loginOldReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\", \"password\":\"" + oldPassword + "\"}"))
            .build();
        HttpResponse<String> loginOldResp = client.send(loginOldReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + loginOldResp.statusCode() +
            "\n\tResponse body: " + loginOldResp.body());
        if (loginOldResp.statusCode() != 401) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Login with old password should have failed with 401!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/login\n\tTrying to log in with first new password: " + newPassword + " -->(should fail)...");
        // 10. Try to log in with new password (should fail)
        HttpRequest loginNewReq = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\", \"password\":\"" + newPassword + "\"}"))
            .build();
        HttpResponse<String> loginNewResp = client.send(loginNewReq, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + loginNewResp.statusCode() +
            "\n\tResponse body: " + loginNewResp.body());
        if (loginNewResp.statusCode() != 401) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Login with first new password should have failed with 401!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/login\n\tTrying to log in with second new password: " + newPassword2 + " -->(should succeed)...");
        // 11. Log in with new password 2
        HttpRequest loginNew2Req = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(
                "{\"email\":\"" + email + "\", \"password\":\"" + newPassword2 + "\"}"))
            .build();
        HttpResponse<String> loginNew2Resp = client.send(loginNew2Req, HttpResponse.BodyHandlers.ofString());
        String loginRespBody2 = loginNew2Resp.body();
        System.out.println(
            "\tResponse status code: " + loginNew2Resp.statusCode() +
            "\n\tResponse body: " + loginNew2Resp.body());
        if (loginNew2Resp.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Login with second new password failed!");
            System.exit(1);
        }
        String jwtToken2 = extractJsonValue(loginRespBody2, "token");
        // String refreshToken2 = extractJsonValue(loginRespBody2, "refresh");
        System.out.println("\n===========================================================");
        System.out.println("POST: /api/auth/delete-account");
        // 12. Delete account
        HttpRequest deleteAccountReq2 = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/delete-account"))
            .header("Authorization", "Bearer " + jwtToken2)
            .DELETE()
            .build();
        HttpResponse<String> deleteAccountResp2 = client.send(deleteAccountReq2, HttpResponse.BodyHandlers.ofString());
        System.out.println(
            "\tResponse status code: " + deleteAccountResp2.statusCode() +
            "\n\tResponse body: " + deleteAccountResp2.body());
        if (deleteAccountResp2.statusCode() != 200) {
            System.err.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX --> Final delete account failed!");
            System.exit(1);
        }

        System.out.println("\n===========================================================");
        System.out.println("Test completed successfully!");
    }

    // Simple JSON value extractor (for demo purposes only)
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf('"', start);
        return json.substring(start, end);
    }
} 