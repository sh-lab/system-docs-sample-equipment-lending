package net.shlab.hogefugapiyo.framework.web;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OneTimeTokenSupport {

    private static final String SESSION_ATTRIBUTE = OneTimeTokenSupport.class.getName() + ".TOKENS";

    private OneTimeTokenSupport() {
    }

    public static String issueToken(HttpSession session, String scope) {
        Map<String, String> tokens = getOrCreateTokens(session);
        String token = UUID.randomUUID().toString();
        tokens.put(scope, token);
        session.setAttribute(SESSION_ATTRIBUTE, tokens);
        return token;
    }

    public static boolean consumeToken(HttpSession session, String scope, String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Map<String, String> tokens = (Map<String, String>) session.getAttribute(SESSION_ATTRIBUTE);
        if (tokens == null) {
            return false;
        }
        String expected = tokens.get(scope);
        if (!token.equals(expected)) {
            return false;
        }
        tokens.remove(scope);
        if (tokens.isEmpty()) {
            session.removeAttribute(SESSION_ATTRIBUTE);
        } else {
            session.setAttribute(SESSION_ATTRIBUTE, tokens);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getOrCreateTokens(HttpSession session) {
        Object value = session.getAttribute(SESSION_ATTRIBUTE);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, String>) map;
        }
        return new HashMap<>();
    }
}
