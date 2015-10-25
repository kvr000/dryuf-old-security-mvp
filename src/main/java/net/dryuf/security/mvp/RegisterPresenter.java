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

import java.util.Map;

import net.dryuf.core.Options;
import net.dryuf.meta.ActionDef;
import net.dryuf.security.form.RegisterForm;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;


public abstract class RegisterPresenter extends net.dryuf.mvp.BeanFormPresenter<RegisterForm>
{
	public				RegisterPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
	}

	@Override
	public RegisterForm		createBackingObject()
	{
		setSelectFieldName("username");
		return new RegisterForm();
	}

	public boolean			processCommon()
	{
		if (Integer.valueOf(this.getRequest().getParamDefault("error", "-1")) == 0) {
			return this.createOkPresenter().process();
		}
		return super.processCommon();
	}

	public Presenter		createOkPresenter()
	{
		return Presenter.createSubPresenter(net.dryuf.security.mvp.RegisterOkPresenter.class, this.parentPresenter, net.dryuf.core.Options.NONE);
	}

	public void			prepare()
	{
		this.getRootPresenter().setActiveField(0, "username");
		super.prepare();
	}

	public boolean			retrieve(Map<String, String> errors, ActionDef action)
	{
		if (!super.retrieve(errors, action))
			return false;
		RegisterForm registerForm = getBackingObject();
		if (!registerForm.getPassword2().equals(registerForm.getPassword())) {
			errors.put("password2", this.localize(RegisterPresenter.class, "Passwords do not match"));
		}
		if (!registerForm.getEmail2().equals(registerForm.getEmail())) {
			errors.put("email2", this.localize(RegisterPresenter.class, "Emails do not match"));
		}
		return errors.isEmpty();
	}

	public boolean			performRegister(ActionDef action)
	{
		int err;
		switch ((err = this.doRegister())) {
		case 0:
			return this.commitOk();

		case 1:
			this.addMessageLocalized(Presenter.MSG_Error, this.formClassName, "User of the same name already exists");
			break;

		default:
			this.addMessage(Presenter.MSG_Error, this.localize(this.formClassName, "Unknown error occurred:")+err);
			break;
		}
		return true;
	}

	public boolean			commitOk()
	{
		this.getRootPresenter().redirect(PageUrl.createRelative("?error=0"));
		return false;
	}

	abstract public int		doRegister();
}
