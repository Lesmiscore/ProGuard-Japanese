/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2016 Eric Lafortune @ GuardSquare
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.obfuscate;

import java.util.*;


/**
 * This <code>NameFactory</code> generates unique short names, using hiragana and katagana
 * characters or hiragana characters only.
 *
 * @author Eric Lafortune
 */
public class SimpleNameFactory implements NameFactory
{

    private static final List cachedMixedCaseNames = new ArrayList();
    private static final List cachedLowerCaseNames = new ArrayList();

    private static final String HIRAGANA_CHARACTERS=new StringBuilder()
            .append("あいうえお")//  A Row
            .append("かきくけこ")// KA Row
            .append("さしすせそ")// SA Row
            .append("たちつてと")// TA Row
            .append("なにぬねの")// NA Row
            .append("はひふへほ")// HA Row
            .append("まみむめも")// MA Row
            .append("やゆよ")    // YA Row
            .append("らりるれろ")// RA Row
            .append("わをん")    // WA Row
            .append("ぁぃぅぇぉ")//  A Row (Sutegana)
            .append("ゃゅょ")    // YA Row (Sutegana)
            .append("ゎ")        // WA Row (Only "WA" has sutegana, but others don't)
            .toString();

    private static final String KATAGANA_CHARACTERS=new StringBuilder()
            .append("アイウエオ")//  A Row
            .append("カキクケコ")// KA Row
            .append("サシスセソ")// SA Row
            .append("タチツテト")// TA Row
            .append("ナニヌネノ")// NA Row
            .append("ハヒフヘホ")// HA Row
            .append("マミムメモ")// MA Row
            .append("ヤユヨ")    // YA Row
            .append("ラリルレロ")// RA Row
            .append("ワヲン")    // WA Row
            .append("ァィゥェォ")//  A Row (Sutegana)
            .append("ヵ")         // KA Row (Only "KA" has sutegana in katagana, but others don't)
            .append("ャュョ")    // YA Row (Sutegana)
            .append("ヮ")        // WA Row (Only "WA" has sutegana, but others don't)
            .toString();

    private static final String ALL_CHARACTERS=HIRAGANA_CHARACTERS+KATAGANA_CHARACTERS;

    private final boolean generateMixedCaseNames;
    private int     index = 0;


    /**
     * Creates a new <code>SimpleNameFactory</code> that generates mixed-case names.
     */
    public SimpleNameFactory()
    {
        this(true);
    }


    /**
     * Creates a new <code>SimpleNameFactory</code>.
     * @param generateMixedCaseNames a flag to indicate whether the generated
     *                               names will be mixed-case, or lower-case only.
     */
    public SimpleNameFactory(boolean generateMixedCaseNames)
    {
        this.generateMixedCaseNames = generateMixedCaseNames;
    }


    // Implementations for NameFactory.

    public void reset()
    {
        index = 0;
    }


    public String nextName()
    {
        return name(index++);
    }


    /**
     * Returns the name at the given index.
     */
    private String name(int index)
    {
        // Which cache do we need?
        List cachedNames = generateMixedCaseNames ?
            cachedMixedCaseNames :
            cachedLowerCaseNames;

        // Do we have the name in the cache?
        if (index < cachedNames.size())
        {
            return (String)cachedNames.get(index);
        }

        // Create a new name and cache it.
        String name = newName(index);
        cachedNames.add(index, name);

        return name;
    }


    /**
     * Creates and returns the name at the given index.
     */
    private String newName(int index)
    {
        // If we're allowed to generate mixed-case names, we can use twice as
        // many characters.
        int totalCharacterCount = generateMixedCaseNames ?
                ALL_CHARACTERS.length() :
                HIRAGANA_CHARACTERS.length();

        int baseIndex = index / totalCharacterCount;
        int offset    = index % totalCharacterCount;

        char newChar = charAt(offset);

        String newName = baseIndex == 0 ?
            new String(new char[] { newChar }) :
            (name(baseIndex-1) + newChar);

        return newName;
    }


    /**
     * Returns the character with the given index, between 0 and the number of
     * acceptable characters.
     */
    private char charAt(int index)
    {
        return (generateMixedCaseNames?ALL_CHARACTERS:HIRAGANA_CHARACTERS).charAt(index);
    }


    public static void main(String[] args)
    {
        System.out.println("Some mixed-case names:");
        printNameSamples(new SimpleNameFactory(true), 60);
        System.out.println("Some lower-case names:");
        printNameSamples(new SimpleNameFactory(false), 60);
        System.out.println("Some more mixed-case names:");
        printNameSamples(new SimpleNameFactory(true), 80);
        System.out.println("Some more lower-case names:");
        printNameSamples(new SimpleNameFactory(false), 80);
    }


    private static void printNameSamples(SimpleNameFactory factory, int count)
    {
        for (int counter = 0; counter < count; counter++)
        {
            System.out.println("  ["+factory.nextName()+"]");
        }
    }
}
