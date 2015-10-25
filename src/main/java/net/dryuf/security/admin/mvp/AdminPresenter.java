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

package net.dryuf.security.admin.mvp;

import net.dryuf.mvp.PresenterDivider;
import net.dryuf.mvp.PresenterElement;
import net.dryuf.mvp.StaticPresenterDivider;


public class AdminPresenter extends net.dryuf.mvp.DividerPresenter
{
	public			AdminPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options, divider);
	}

	protected static PresenterDivider divider = new StaticPresenterDivider(new PresenterElement[]{
		PresenterElement.createClassed("",		true,	"admin",	AdminMainPresenter.class,		net.dryuf.core.Options.NONE),
		PresenterElement.createClassed("translation",	true,	"admin",	AdminTranslationPresenter.class,	net.dryuf.core.Options.NONE),
		PresenterElement.createClassed("roles",		true,	"admin",	AdminRolesPresenter.class,		net.dryuf.core.Options.NONE),
	});
}
