
package dev.roomwarrior.wedding.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RequestRateLimitFilter extends OncePerRequestFilter {

    private final LoadingCache<String, Integer> requestCountsCache;
    private final ConcurrentHashMap<String, Long> blockedIps = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 3;
    private static final long BLOCK_DURATION_MS = TimeUnit.HOURS.toMillis(1);

    public RequestRateLimitFilter() {
        super();
        requestCountsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getMethod().equals("POST") && request.getRequestURI().equals("/rsvp")) {
            String clientIp = getClientIP(request);

            // Проверяем, не заблокирован ли IP
            if (isIpBlocked(clientIp)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Ваш IP-адрес заблокирован за превышение лимита запросов. " +
                        "Попробуйте позже.");
                return;
            }

            int requests = requestCountsCache.getUnchecked(clientIp);
            if (requests >= MAX_REQUESTS_PER_MINUTE) {
                blockIp(clientIp);
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Ваш IP-адрес заблокирован на 1 час за превышение лимита запросов.");
                return;
            }

            requestCountsCache.put(clientIp, requests + 1);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        }
        return xForwardedForHeader.split(",")[0].trim();
    }

    private boolean isIpBlocked(String ip) {
        Long blockExpiration = blockedIps.get(ip);
        if (blockExpiration != null) {
            if (System.currentTimeMillis() < blockExpiration) {
                return true;
            } else {
                blockedIps.remove(ip);
            }
        }
        return false;
    }

    private void blockIp(String ip) {
        blockedIps.put(ip, System.currentTimeMillis() + BLOCK_DURATION_MS);
    }
}
