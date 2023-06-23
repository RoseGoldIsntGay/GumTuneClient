package rosegold.gumtuneclient.config.pages;

import cc.polyfrost.oneconfig.gui.elements.BasicButton;
import cc.polyfrost.oneconfig.gui.elements.text.TextInputField;
import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import kotlin.Triple;
import net.minecraft.util.Tuple;
import rosegold.gumtuneclient.modules.macro.GemstoneMacro;
import rosegold.gumtuneclient.utils.ModUtils;
import rosegold.gumtuneclient.utils.objects.Waypoint;
import rosegold.gumtuneclient.utils.objects.WaypointList;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class GemstoneMacroAOTVRoutes extends Page {
    private static final CopyOnWriteArrayList<Tuple<WaypointList, TextInputField>> textInputFields = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Triple<WaypointList, Waypoint, TextInputField>> nameInputFields = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Triple<WaypointList, Waypoint, TextInputField>> xInputFields = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Triple<WaypointList, Waypoint, TextInputField>> yInputFields = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Triple<WaypointList, Waypoint, TextInputField>> zInputFields = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Route> routes = new CopyOnWriteArrayList<>();
    private final BasicButton addNewList = new BasicButton(120, BasicButton.SIZE_36, "Add New List", null, null, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY);

    public GemstoneMacroAOTVRoutes() {
        super("Gemstone Macro AOTV Routes");
        addNewList.setClickAction(() -> {
            GemstoneMacro.allPaths.add(new WaypointList("New Route"));
            redrawRoutes();
            GemstoneMacro.saveConfig();
        });
        redrawRoutes();
    }

    public static void redrawRoutes() {
        routes.clear();

        textInputFields.clear();
        nameInputFields.clear();
        xInputFields.clear();
        yInputFields.clear();
        zInputFields.clear();

        for (WaypointList list : GemstoneMacro.allPaths) {
            Route route = new Route(list.name);
            route.setEnabled(list.enabled);
            route.setShowCoords(list.showCoords);

            if (list.showCoords && list.waypoints != null) {
                for (Map.Entry<Integer, Waypoint> entry : list.waypoints.entrySet()) {
                    Integer index = entry.getKey();
                    Waypoint waypoint = entry.getValue();

                    RouteWaypoint routeWaypoint = new RouteWaypoint();

                    TextInputField indexField = new TextInputField(150, 40, String.valueOf(index), false, false);
                    nameInputFields.add(new Triple<>(list, waypoint, indexField));
                    routeWaypoint.nameField = indexField;

                    TextInputField xField = new TextInputField(60, 40, String.valueOf(waypoint.x), false, false);
                    xInputFields.add(new Triple<>(list, waypoint, xField));
                    routeWaypoint.xField = xField;

                    TextInputField yField = new TextInputField(60, 40, String.valueOf(waypoint.y), false, false);
                    yInputFields.add(new Triple<>(list, waypoint, yField));
                    routeWaypoint.yField = yField;

                    TextInputField zField = new TextInputField(60, 40, String.valueOf(waypoint.z), false, false);
                    zInputFields.add(new Triple<>(list, waypoint, zField));
                    routeWaypoint.zField = zField;

                    BasicButton deleteButton = new BasicButton(80, BasicButton.SIZE_36, "Delete", null, null, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE);
                    deleteButton.setClickAction(() -> {
                        list.waypoints.remove(index);
                        redrawRoutes();
                    });
                    routeWaypoint.deleteButton = deleteButton;

                    route.addWaypoint(routeWaypoint);
                }
            } else {
                route.waypoints = null;
            }

            BasicButton selected = new BasicButton(80, BasicButton.SIZE_36, list.enabled ? "Selected" : "Select", null, null, BasicButton.ALIGNMENT_CENTER, list.enabled ? ColorPalette.PRIMARY : ColorPalette.SECONDARY);
            if (list.enabled) {
                selected.setToggleable(false);
                selected.setToggled(true);
                selected.disable(true);
            } else {
                selected.setToggleable(true);
                selected.disable(false);
            }
            selected.setClickAction(() -> {
                GemstoneMacro.allPaths.forEach(waypointList -> waypointList.enabled = false);
                list.enabled = true;
                redrawRoutes();
            });
            route.selected = selected;

            route.nameField = new TextInputField(300, 40, list.name, false, false);
            textInputFields.add(new Tuple<>(list, route.nameField));

            BasicButton expandButton = new BasicButton(80, BasicButton.SIZE_36, list.showCoords ? "Hide" : "Expand", null, null, BasicButton.ALIGNMENT_CENTER, list.showCoords ? ColorPalette.PRIMARY : ColorPalette.SECONDARY);
            expandButton.setToggleable(true);
            expandButton.setClickAction(() -> {
                if (Objects.equals(expandButton.getText(), "Expand")) {
                    GemstoneMacro.allPaths.forEach(waypointList -> waypointList.showCoords = false);
                    list.showCoords = true;
                } else if (Objects.equals(expandButton.getText(), "Hide")) {
                    list.showCoords = false;
                }
                redrawRoutes();
            });
            route.expandButton = expandButton;

            BasicButton deleteButton = new BasicButton(80, BasicButton.SIZE_36, "Delete", null, null, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE);
            deleteButton.setClickAction(() -> {
                GemstoneMacro.allPaths.remove(list);
                redrawRoutes();
            });
            route.deleteButton = deleteButton;

            routes.add(route);
        }
    }

    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        int iX = x + 16;
        int iY = y + 64;

        if (GemstoneMacro.allPaths == null || GemstoneMacro.allPaths.size() == 0 || routes.size() == 0) {
            float widthOfText = NanoVGHelper.INSTANCE.getTextWidth(vg, "No routes found!", 24f, Fonts.BOLD);
            NanoVGHelper.INSTANCE.drawText(vg, "No routes found!", 512 - (widthOfText/2), 300, Color.WHITE.getRGB(), 24f, Fonts.BOLD);
            return;
        }

        for (Route list : routes) {
            NanoVGHelper.INSTANCE.drawRoundedRect(vg, iX, iY, 1008, 50, Color.DARK_GRAY.getRGB(), 12);

            list.selected.draw(vg, iX + 16, iY + 6, inputHandler);

            list.nameField.draw(vg, iX + 110, iY + 6, inputHandler);

            list.expandButton.draw(vg, x + 1008 - 80, iY + 6, inputHandler);

            list.deleteButton.draw(vg, x + 1008 - 80 - 90, iY + 6, inputHandler);

            if (list.showCoords) {
                int i = 0;
                for (RouteWaypoint waypoint : list.waypoints.values()) {
                    int indendX = iX + 32;
                    NanoVGHelper.INSTANCE.drawRoundedRect(vg, indendX, iY + 56 + (i * 56), 944, 50, new Color(50,50,50, 120).getRGB(), 8);

                    indendX += 16;

                    NanoVGHelper.INSTANCE.drawText(vg, "Index: ", indendX, iY + 56 + (i * 56) + 6 + 20, Color.WHITE.getRGB(), 18f, Fonts.BOLD);
                    indendX += NanoVGHelper.INSTANCE.getTextWidth(vg, "Index: ", 18f, Fonts.BOLD) + 10;
                    waypoint.nameField.draw(vg, indendX, iY + 56 + (i * 56) + 6, inputHandler);

                    indendX += 160;
                    NanoVGHelper.INSTANCE.drawText(vg, "X: ", indendX, iY + 56 + (i * 56) + 6 + 20, Color.WHITE.getRGB(), 18f, Fonts.BOLD);
                    indendX += NanoVGHelper.INSTANCE.getTextWidth(vg, "X: ", 18f, Fonts.BOLD) + 10;
                    waypoint.xField.draw(vg, indendX, iY + 56 + (i * 56) + 6, inputHandler);
                    indendX += 70;
                    NanoVGHelper.INSTANCE.drawText(vg, "Y: ", indendX, iY + 56 + (i * 56) + 6 + 20, Color.WHITE.getRGB(), 18f, Fonts.BOLD);
                    indendX += NanoVGHelper.INSTANCE.getTextWidth(vg, "Y: ", 18f, Fonts.BOLD) + 10;
                    waypoint.yField.draw(vg, indendX, iY + 56 + (i * 56) + 6, inputHandler);
                    indendX += 70;
                    NanoVGHelper.INSTANCE.drawText(vg, "Z: ", indendX, iY + 56 + (i * 56) + 6 + 20, Color.WHITE.getRGB(), 18f, Fonts.BOLD);
                    indendX += NanoVGHelper.INSTANCE.getTextWidth(vg, "Z: ", 18f, Fonts.BOLD) + 10;
                    waypoint.zField.draw(vg, indendX, iY + 56 + (i * 56) + 6, inputHandler);

                    waypoint.deleteButton.draw(vg, x + 944 - 80 - 30 - 40, iY + 56 + (i * 56) + 6, inputHandler);

                    i++;
                }
                iY += (list.waypoints.size() * 56);
            }

            iY += 64;
        }
    }

    @Override
    public int drawStatic(long vg, int x, int y, InputHandler inputHandler) {
        addNewList.draw(vg, x + 8, y + 8, inputHandler);
        return addNewList.getHeight() + 16;
    }

    @Override
    public int getMaxScrollHeight() {
        int scrollHeight = addNewList.getHeight() + 32;
        for (Route list : routes) {
            scrollHeight += 64;
            if (list.showCoords) {
                scrollHeight += (list.waypoints.size() * 56);
            }
        }
        return scrollHeight;
    }

    @Override
    public void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        textInputFields.forEach(tuple -> {
            tuple.getSecond().keyTyped(key, keyCode);
            if (!tuple.getSecond().isToggled() && tuple.getSecond().getInput().length() > 0) {
                tuple.getFirst().name = tuple.getSecond().getInput();
                redrawRoutes();
                GemstoneMacro.saveConfig();
            }
        });
        xInputFields.forEach(tuple -> {
            tuple.getThird().keyTyped(key, keyCode);
            if (!tuple.getThird().isToggled() && tuple.getThird().getInput().length() > 0) {
                try {
                    tuple.getFirst().getValue(tuple.getFirst().getValue(tuple.getSecond())).x = Integer.parseInt(tuple.getThird().getInput());
                    redrawRoutes();
                    GemstoneMacro.saveConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        yInputFields.forEach(tuple -> {
            tuple.getThird().keyTyped(key, keyCode);
            if (!tuple.getThird().isToggled() && tuple.getThird().getInput().length() > 0) {
                try {
                    tuple.getFirst().getValue(tuple.getFirst().getValue(tuple.getSecond())).y = Integer.parseInt(tuple.getThird().getInput());
                    redrawRoutes();
                    GemstoneMacro.saveConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        zInputFields.forEach(tuple -> {
            tuple.getThird().keyTyped(key, keyCode);
            if (!tuple.getThird().isToggled() && tuple.getThird().getInput().length() > 0) {
                try {
                    tuple.getFirst().getValue(tuple.getFirst().getValue(tuple.getSecond())).y = Integer.parseInt(tuple.getThird().getInput());
                    redrawRoutes();
                    GemstoneMacro.saveConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        nameInputFields.forEach(tuple -> {
            tuple.getThird().keyTyped(key, keyCode);
            if (!tuple.getThird().isToggled() && tuple.getThird().getInput().length() > 0) {
                if (isInteger(tuple.getThird().getInput()) && Integer.parseInt(tuple.getThird().getInput()) > -1) {
                    Waypoint waypoint = tuple.getFirst().getValue(tuple.getFirst().getValue(tuple.getSecond()));
                    tuple.getFirst().getValue(tuple.getFirst().getValue(tuple.getSecond())).name = tuple.getThird().getInput();
                    tuple.getFirst().removeValue(waypoint);
                    tuple.getFirst().waypoints.put(Integer.valueOf(tuple.getThird().getInput()), waypoint);
                    redrawRoutes();
                    GemstoneMacro.saveConfig();
                } else {
                    ModUtils.sendMessage("Invalid number " + tuple.getThird().getInput());
                }
            }
        });
    }

    private static class RouteWaypoint {
        private TextInputField nameField;
        private Waypoint waypoint;
        private TextInputField xField;
        private TextInputField yField;
        private TextInputField zField;
        private BasicButton deleteButton;
    }

    private static class Route {
        private String name;
        private HashMap<Integer, RouteWaypoint> waypoints = new HashMap<>();
        private boolean enabled = false;
        private boolean showCoords = false;

        public BasicButton selected;
        public TextInputField nameField;
        public BasicButton expandButton;
        public BasicButton deleteButton;

        public Route(String name) {
            this.name = name;
        }

        public void setShowCoords(boolean showCoords) {
            this.showCoords = showCoords;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void addWaypoint(RouteWaypoint waypoint) {
            this.waypoints.put(getEmptyIndex(), waypoint);
        }

        public Integer getEmptyIndex() {
            if (this.waypoints.isEmpty()) return 0;

            for (int i = 0; i < getLastIndex(); i++) {
                if (!this.waypoints.containsKey(i)) return i;
            }

            return getLastIndex() + 1;
        }

        public Integer getLastIndex() {
            int lastIndex = 0;

            for (int index : this.waypoints.keySet()) {
                if (index > lastIndex) lastIndex = index;
            }

            return lastIndex;
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
