/*    
 *    MediathekView
 *    Copyright (C) 2008   W. Xaver
 *    W.Xaver[at]googlemail.com
 *    http://zdfmediathk.sourceforge.net/
 *    
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mediathek.tool;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import mSearch.tool.Log;
import mediathek.config.Icons;
import mediathek.config.Daten;
import mediathek.daten.DatenProg;

public class CellRendererProgramme extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;
    
    private Daten daten;
    private static ImageIcon ja_16 = null;
    private static ImageIcon nein_12 = null;

    public CellRendererProgramme(Daten d) {
        daten = d;
        ja_16 = Icons.ICON_TABELLE_EIN;
        nein_12 = Icons.ICON_TABELLE_AUS;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        setIcon(null);
        super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        try {
            int c = table.convertColumnIndexToModel(column);
            if (c == DatenProg.PROGRAMM_RESTART || c == DatenProg.PROGRAMM_DOWNLOADMANAGER) {
                setHorizontalAlignment(CENTER);
                if (getText().equals(Boolean.TRUE.toString())) {
                    setIcon(ja_16);
                } else {
                    setIcon(nein_12);
                }
                setText("");
            }
        } catch (Exception ex) {
            Log.errorLog(338740095, ex);
        }
        return this;
    }
}
