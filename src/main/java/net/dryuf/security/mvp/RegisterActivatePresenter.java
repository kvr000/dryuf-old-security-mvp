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


import net.dryuf.mvp.Presenter;

public abstract class RegisterActivatePresenter extends net.dryuf.mvp.ChildPresenter
{
	public			RegisterActivatePresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);
	}

	public boolean			processCommon()
	{
		String username = this.getRequest().getParam("username");
		String activationCode = this.getRequest().getParam("code");
		this.error = this.doActivate(username, activationCode);
		return super.processCommon();
	}

	public void			prepare()
	{
		super.prepare();
		switch (this.error) {
		case 0:
			this.addMessageLocalized(Presenter.MSG_Info, RegisterActivatePresenter.class, "Activation successfull, you can login now");
			break;

		case 1:
			this.addMessageLocalized(Presenter.MSG_Error, RegisterActivatePresenter.class, "Unknown user");
			break;

		case 6:
			this.addMessageLocalized(Presenter.MSG_Error, RegisterActivatePresenter.class, "Wrong activation code");
			break;

		default:
			this.addMessage(Presenter.MSG_Error, this.localize(RegisterActivatePresenter.class, "Unknown error:")+" "+error+"");
			break;
		}
	}

	abstract public int		doActivate(String username, String activationCode);

	protected int			error;
}
