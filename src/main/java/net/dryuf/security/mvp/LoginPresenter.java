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

package net.dryuf.security.mvp;

import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;
import net.dryuf.security.form.LoginForm;
import net.dryuf.meta.ActionDef;


public abstract class LoginPresenter extends net.dryuf.mvp.BeanFormPresenter<LoginForm>
{
	public				LoginPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
	}

	@Override
	public LoginForm		createBackingObject()
	{
		setSelectFieldName("username");
		return new LoginForm();
	}

	@Override
	public void			prepare()
	{
		this.getRootPresenter().setActiveField(0, getFormWebPrefix()+"username");
		super.prepare();
	}

	@Override
	public String			initData()
	{
		LoginForm loginForm = needBackingObject();
		loginForm.setRedir(this.getRequest().getParamDefault("redir", null));
		return null;
	}

	public boolean			performLogin(ActionDef action)
	{
		this.getRootPresenter().forceSession();
		String err;
		if ((err = this.doLogin()) == null)
			return false;
		this.addMessage(Presenter.MSG_Error, err);
		return true;
	}

	abstract public	String		doLogin();
}
