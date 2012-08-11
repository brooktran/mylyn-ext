/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.mylyn.bugview.internal;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;

public interface IBugView {

  void setActiveQuery( IRepositoryQuery query );

  IRepositoryQuery getActiveQuery();

}
