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
package gr.auth.csd.intelligence.cgsp.experiments;

import java.io.IOException;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class cgs_p {
    public static void main(String[] args) throws IOException, InterruptedException {
        int i=6;
        switch (i) {
            case 0:
                ExperimentSection5_3.main(args);
                break;
            case 1:
                ExperimentSection5_4.main(args);
                break;
            case 2:
                ExperimentSection5_4CVB0.main(args);
                break;
            case 3:
                ExperimentSection5_5.main(args);
                break;
            case 4:
                CGS_pWithMALLET.main(args);
                break;
            case 5:
                CGS_pWithWarpLDA.main(args);
                break;
            case 6:
                MultiLabelExperiment.main(args);
                break;
            default:
                break;
        }
        
    }
}
