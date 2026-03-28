package net.shlab.hogefugapiyo.framework.core.controller;

import net.shlab.hogefugapiyo.equipmentlending.presentation.route.RoutePaths;

/**
 * コントローラの共通基底クラス。
 * 共通処理やユーティリティを定義する。
 */
public abstract class AbstractBaseController {

    protected String redirectTo(String viewName) {
        return "redirect:" + RoutePaths.fromView(viewName);
    }
}
