package net.shlab.hogefugapiyo.equipmentlending.presentation.controller;

import net.shlab.hogefugapiyo.equipmentlending.application.BusinessException;
import net.shlab.hogefugapiyo.equipmentlending.application.SystemException;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.SecurityRouteResolver;
import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * グローバル例外ハンドラ。
 *
 * <p>各 Controller で個別に処理されなかった例外を一元的に補足し、
 * 業務例外はホーム画面へのリダイレクト、システム例外はエラー画面への遷移を行う。
 *
 * <p>Spring Security が管理する認証・認可例外はこのハンドラでは処理せず、
 * Spring Security のハンドラチェーンに委譲する。
 *
 * <p>設計書:
 * <ul>
 *   <li>{@code docs/02_architecture/error-handling.md}</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final SecurityRouteResolver securityRouteResolver;

    public GlobalExceptionHandler(SecurityRouteResolver securityRouteResolver) {
        this.securityRouteResolver = securityRouteResolver;
    }

    /**
     * 業務例外のフォールバックハンドラ。
     *
     * <p>Controller で個別に処理されなかった {@link BusinessException} を補足し、
     * 利用者のホーム画面へエラーメッセージ付きでリダイレクトする。
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(
            BusinessException ex,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.warn("業務例外が発生しました: messageId={}", ex.messageId());
        String homePath = resolveHomePath(userPrincipal);
        return "redirect:" + homePath + "?errorMessageId=" + ex.messageId();
    }

    /**
     * システム例外のハンドラ。
     *
     * <p>{@link SystemException} を補足し、ERROR レベルでスタックトレース付きログを出力した上で
     * 汎用エラー画面へ遷移する。
     */
    @ExceptionHandler(SystemException.class)
    public String handleSystemException(SystemException ex) {
        log.error("システム例外が発生しました", ex);
        return "error";
    }

    /**
     * 未分類例外のフォールバックハンドラ。
     *
     * <p>上記いずれにも該当しない想定外の例外を補足し、汎用エラー画面へ遷移する。
     * Spring Security の認可例外は再スローし、Security のハンドラチェーンに委譲する。
     */
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex) throws Exception {
        if (ex instanceof AccessDeniedException) {
            throw ex;
        }
        log.error("予期しない例外が発生しました", ex);
        return "error";
    }

    private String resolveHomePath(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "/login";
        }
        return securityRouteResolver.resolveHomePath(userPrincipal);
    }
}
