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

import net.dryuf.meta.ActionDef;
import net.dryuf.security.form.ForgotUpdatePasswordForm;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;


public abstract class ForgotUpdatePasswordPresenter extends net.dryuf.mvp.BeanFormPresenter<ForgotUpdatePasswordForm>
{
	public				ForgotUpdatePasswordPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);
	}

	@Override
	public ForgotUpdatePasswordForm	createBackingObject()
	{
		setSelectFieldName("password");
		return new ForgotUpdatePasswordForm();
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
		return Presenter.createSubPresenter(net.dryuf.security.mvp.ForgotUpdatePasswordOkPresenter.class, this.parentPresenter, net.dryuf.core.Options.NONE);
	}

	@Override
	public String			initData()
	{
		ForgotUpdatePasswordForm forgotUpdatePasswordForm = getBackingObject();
		forgotUpdatePasswordForm.setUsername(this.getRequest().getParam("username"));
		forgotUpdatePasswordForm.setActivityCode(this.getRequest().getParam("code"));
		return null;
	}

	public void			prepare()
	{
		this.getRootPresenter().setActiveField(0, "password");
		super.prepare();
	}

	@Override
	public boolean			retrieve(Map<String, String> errors, ActionDef action)
	{
		if (!super.retrieve(errors, action))
			return false;
		ForgotUpdatePasswordForm forgotUpdatePasswordForm = getBackingObject();
		if (!forgotUpdatePasswordForm.getPassword2().equals(forgotUpdatePasswordForm.getPassword())) {
			errors.put("password2", this.localize(ForgotUpdatePasswordPresenter.class, "Passwords do not match"));
		}
		return errors.isEmpty();
	}

	public boolean			performUpdate(ActionDef action)
	{
		int err;
		switch ((err = this.doUpdate())) {
		case 0:
			return this.commitOk();

		case 1:
			this.addMessageLocalized(Presenter.MSG_Error, this.formClassName, "User does not exist");
			break;

		case 6:
			this.addMessageLocalized(Presenter.MSG_Error, this.formClassName, "Wrong activation code");
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

	public abstract int		doUpdate();
};


