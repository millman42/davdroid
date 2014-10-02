/*******************************************************************************
 * Copyright (c) 2014 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Richard Hirner (bitfire web engineering) - initial API and implementation
 ******************************************************************************/
package at.bitfire.davdroid.mirakel.webdav;

public class DavIncapableException extends DavException {
	private static final long serialVersionUID = -7199786680939975667L;
	
	/* used to indicate that the server doesn't support DAV */
	
	public DavIncapableException(String msg) {
		super(msg);
	}
}