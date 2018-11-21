package com.test.cinema.integrator.util;

import com.test.cinema.model.request.RequestBean;
import org.springframework.test.web.servlet.ResultActions;

/**
 * @author Ovchinnikov Vladimir email ovchinnikovvg@altarix.ru
 */
public interface Function {

    ResultActions exec(RequestBean bean) throws Exception;
}
