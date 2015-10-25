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

import javax.validation.constraints.NotNull;

import com.google.common.base.Function;

import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.PresenterDivider;
import net.dryuf.mvp.PresenterElement;
import net.dryuf.mvp.StaticPresenterDivider;


public class CommonLoginDividerPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				CommonLoginDividerPresenter(@NotNull Presenter parentPresenter_, @NotNull Options options)
	{
		super(parentPresenter_, options);
	}

	@Override
	public boolean			processMore(String element)
	{
		return divider.processConsumed(this, element);
	}

	@Override
	public boolean			processFinal()
	{
		this.isFinal = true;
		return divider.processConsumed(this, null);
	}

	@Override
	public void			render()
	{
		super.render();

		if (this.isFinal) {
			this.outputFormat("<a href=\"forgotpassword/\">%W</a><br/>", CommonLoginDividerPresenter.class, "Forgot password?");
			this.outputFormat("<a href=\"register/\">%W %W</a><br/>", CommonLoginDividerPresenter.class, "Not registered yet?", CommonLoginDividerPresenter.class, "Register now.");
		}
	}

	protected boolean		isFinal = false;

	protected static PresenterDivider divider = new StaticPresenterDivider(new net.dryuf.mvp.PresenterElement[] {
			PresenterElement.createFunction("",			true,	"guest",	new Function<Presenter, Presenter>() { public Presenter apply(Presenter parent) { return new CommonLoginPresenter(parent, Options.NONE); } }),
			PresenterElement.createClassed("register",		true,	"guest",	net.dryuf.security.mvp.CommonRegisterPresenter.class,			net.dryuf.core.Options.NONE),
			PresenterElement.createClassed("registerok",		true,	"guest",	net.dryuf.security.mvp.RegisterOkPresenter.class,			net.dryuf.core.Options.NONE),
			PresenterElement.createClassed("registeractivate",	true,	"guest",	net.dryuf.security.mvp.CommonRegisterActivatePresenter.class,		net.dryuf.core.Options.NONE),
			PresenterElement.createClassed("forgotpassword",	true,	"guest",	net.dryuf.security.mvp.CommonForgotPasswordPresenter.class,		net.dryuf.core.Options.NONE),
			PresenterElement.createClassed("forgotupdatepassword",	true,	"guest",	net.dryuf.security.mvp.CommonForgotUpdatePasswordPresenter.class,	net.dryuf.core.Options.NONE),
	}).setPageLocalizeClass(CommonLoginDividerPresenter.class);
}
