/*
 * Copyright (c) 2010 iceScrum Technologies.
 *
 * This file is part of iceScrum.
 *
 * iceScrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * iceScrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with iceScrum.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Authors:
 *
 * Stephane Maldini (stephane.maldini@icescrum.com)
 */

package org.icescrum.presentation

import org.icescrum.core.domain.Product

class IceScrumFilters {

  def securityService
  def springSecurityService

  def filters = {
    pkey(controller: 'scrumOS', action: 'index') {
      before = {
        if (params.product) {
          params.product = params.product.decodeProductKey()
          if (!params.product) {
            render(status: 404)
            return
          }

        }
      }
    }

    webservices(uri: '/ws/**') {
      before = {
        def webservices = false
        if (params.product) {
            params.product = params.product.decodeProductKey()
             webservices = Product.createCriteria().get {
                              eq 'id', params.product.toLong()
                                preferences {
                                    projections {
                                        property 'webservices'
                                    }
                                }
                              cache true
                            }
            if (!webservices){
                render(status: 503)
            }else{
                if(request.format == 'xml' && params.values){
                    params.remove('values')?.each{ k, v ->
                        params."${k}" = v
                    }
                }
            }
        }
        return webservices
      }
    }

    permissions(controller: '*', action: '*') {
      before = {
        securityService.filterRequest()
        return
      }
    }

    pkeyFeed(controller: 'project', action: 'feed') {
      before = {
        if (params.product) {
          params.product = params.product.decodeProductKey()
          if (!params.product) {
            render(status: 404)
            return
          }

        }
      }

    }
  }

}
