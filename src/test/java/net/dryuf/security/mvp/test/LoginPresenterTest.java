/*
 * Dryuf framework
 *
 * ----------------------------------------------------------------------------------
 *
 * Copyright (C) 2000-2015 Zbyněk Vyškovský
 *
 * ----------------------------------------------------------------------------------
 *
 * LICENSE:
 *
 * This file is part of Dryuf
 *
 * Dryuf is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * Dryuf is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dryuf; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * @author	2000-2015 Zbyněk Vyškovský
 * @link	mailto:kvr@matfyz.cz
 * @link	http://kvr.matfyz.cz/software/java/dryuf/
 * @link	http://github.com/dryuf/
 * @license	http://www.gnu.org/licenses/lgpl.txt GNU Lesser General Public License v3
 */

package net.dryuf.security.mvp.test;

import net.dryuf.mvp.tenv.PresenterTenvObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.dryuf.core.Options;
import net.dryuf.security.form.LoginForm;
import net.dryuf.security.mvp.CommonLoginPresenter;
import net.dryuf.security.mvp.LoginPresenter;
import net.dryuf.tenv.DAssert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class LoginPresenterTest extends PresenterTenvObject
{
	@Test
	public void			testRender() throws Exception
	{
		LoginPresenter presenter = new CommonLoginPresenter(createRootPresenter(), Options.NONE);
		DAssert.assertTrue(presenter.process(), "process true");
	}

	@Test
	public void			testProcess() throws Exception
	{
		LoginPresenter presenter = new CommonLoginPresenter(createRootPresenter(), Options.NONE);
		LoginForm form = new LoginForm();
		form.setUsername("guest");
		form.setPassword("guest");
		mockRequest.addFormObject(LoginPresenter.formatFormPrefix(form.getClass().getName()), form);
		mockRequest.addParam(LoginPresenter.formatFormPrefix(form.getClass().getName())+"login", "login");
		DAssert.assertFalse(presenter.process(), "process false");
	}
}
