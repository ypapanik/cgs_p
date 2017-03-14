/* 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.auth.csd.intelligence.cgsp.mlclassification;

import gr.auth.csd.intelligence.cgsp.mlclassification.models.Model;
import java.util.concurrent.Callable;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */

public class CallableInferencer implements Callable<Boolean>{	

    /**
     *
     */
    protected Model newModel;
    private final int from, to;
    private final int threads;
    private final int mod;

    /**
     *
     * @param model
     * @param from
     * @param to
     * @param threads
     * @param mod
     */
    public CallableInferencer(Model model, int from, int to, int threads, int mod) {
        newModel = model;
        this.from = from;
        this.to = to;
        this.threads = threads;
        this.mod = mod;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Boolean call() {
        for (int i = from; i <= to; i++) {
            if (i % threads == mod) {
                newModel.update(i);
            }
        }
        return true;
    }
}
