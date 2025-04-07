package gui.color;

import gui.color.palettes.DefaultColorPalette;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColorMgr {
    private ArrayList<JComponent> components;
    private ArrayList<JFrame> frames;

    public static final String[] COLOR_PALETTE_NAMES;
    private static final Map<String, Class<JmpGuiColorPalette>> MAP;

    public ColorMgr() {
        components = new ArrayList<>();
        frames = new ArrayList<>();
    }

    public <C extends JComponent> void add(C comp) {
        if (comp instanceof JmpColored) {
            components.add(comp);
        }
    }

    public void add(JFrame frame) {
        if (frame instanceof JmpColored) {
            frames.add(frame);
        }
    }

    public void changeColor(String str) {
        Class<JmpGuiColorPalette> clazz = MAP.get(str);
        if (clazz == null) return;

        JmpGuiColorPalette cp;
        try {
            cp = clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException ignored) {
            return;
        }


        changeColor(cp);
    }

    public <CP extends JmpGuiColorPalette> void changeColor(CP colorPalette) {
        for (JComponent comp : components) {
            if (comp instanceof JmpColored) {
                ((JmpColored) comp).updateColors(colorPalette);
            }
        }

        for (JFrame frame : frames) {
            if (frame instanceof JmpColored) {
                ((JmpColored) frame).updateColors(colorPalette);
            }
        }
    }

    static {
        Stream<Class<JmpGuiColorPalette>> stream = generateColorPaletteStream();
        List<Class<JmpGuiColorPalette>> l = stream.toList();

        COLOR_PALETTE_NAMES = l.stream()
                .map(ColorMgr::getPaletteName)
                .filter(str -> !str.isEmpty())
                .toArray(String[]::new);

        MAP = l.stream()
                .collect(Collectors.toMap(ColorMgr::getPaletteName, clazz -> (clazz)));
    }

    static public String[] getColorPaletteNames() {
        return COLOR_PALETTE_NAMES;
    }

    // in reference to https://www.baeldung.com/java-find-all-classes-in-package
    static public Stream<Class<JmpGuiColorPalette>> generateColorPaletteStream() {
        String packageName = DefaultColorPalette.class.getPackage().getName();
        System.out.println(packageName);

        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName));
    }

    static private <T extends JmpGuiColorPalette> Class<T> getClass(String className, String packageName) {
        try {
            Class<?> clazz = Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));

            return JmpGuiColorPalette.class.isAssignableFrom(clazz) ? (Class<T>) clazz : null;
        } catch (ClassNotFoundException ignored) {
        }

        return null;
    }

    static private <T extends JmpGuiColorPalette> String getPaletteName(Class<T> clazz) {
        try {
            if (clazz != null) {
                JmpGuiColorPalette palette = clazz.getDeclaredConstructor().newInstance();
                return palette.getPaletteName();
            }
        } catch (InstantiationException | IllegalAccessException
                 | InvocationTargetException | NoSuchMethodException ignored
        ) {
        }
        return "";
    }
}
