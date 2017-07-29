package com.le.dts.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * �й��ַ���Ĺ����ࡣ
 * 
 * <p>
 * ������е�ÿ�����������ԡ���ȫ���ش���<code>null</code>������׳�<code>NullPointerException</code>��
 * </p>
 *
 * @author Michael Zhou
 * @version $Id: StringUtil.java 1149 2004-08-10 02:01:41Z baobao $
 */
public class StringUtil {
    /* ============================================================================ */
    /*  ������singleton��                                                           */
    /* ============================================================================ */

    /** ���ַ� */
    public static final String EMPTY_STRING = "";

    /* ============================================================================ */
    /*  �пպ���                                                                  */
    /*                                                                              */
    /*  ���·��������ж�һ���ַ��Ƿ�Ϊ��                                          */
    /*  1. null                                                                     */
    /*  2. empty - ""                                                               */
    /*  3. blank - "ȫ���ǿհ�" - �հ���Character.isWhitespace���塣              */
    /* ============================================================================ */

    /**
     * ����ַ��Ƿ�Ϊ<code>null</code>����ַ�<code>""</code>��
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ���Ϊ��, �򷵻�<code>true</code>
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.length() == 0));
    }

    /**
     * ����ַ��Ƿ���<code>null</code>�Ϳ��ַ�<code>""</code>��
     * <pre>
     * StringUtil.isEmpty(null)      = false
     * StringUtil.isEmpty("")        = false
     * StringUtil.isEmpty(" ")       = true
     * StringUtil.isEmpty("bob")     = true
     * StringUtil.isEmpty("  bob  ") = true
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ���Ϊ��, �򷵻�<code>true</code>
     */
    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.length() > 0));
    }

    /**
     * ����ַ��Ƿ��ǿհף�<code>null</code>�����ַ�<code>""</code>��ֻ�пհ��ַ�
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ���Ϊ�հ�, �򷵻�<code>true</code>
     */
    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * ����ַ��Ƿ��ǿհף�<code>null</code>�����ַ�<code>""</code>��ֻ�пհ��ַ�
     * <pre>
     * StringUtil.isBlank(null)      = false
     * StringUtil.isBlank("")        = false
     * StringUtil.isBlank(" ")       = false
     * StringUtil.isBlank("bob")     = true
     * StringUtil.isBlank("  bob  ") = true
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ���Ϊ�հ�, �򷵻�<code>true</code>
     */
    public static boolean isNotBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /* ============================================================================ */
    /*  Ĭ��ֵ����                                                                */
    /*                                                                              */
    /*  ���ַ�Ϊnull��empty��blankʱ�����ַ�ת����ָ����Ĭ���ַ�            */
    /* ============================================================================ */

    /**
     * ����ַ���<code>null</code>���򷵻ؿ��ַ�<code>""</code>�����򷵻��ַ��?
     * <pre>
     * StringUtil.defaultIfNull(null)  = ""
     * StringUtil.defaultIfNull("")    = ""
     * StringUtil.defaultIfNull("  ")  = "  "
     * StringUtil.defaultIfNull("bat") = "bat"
     * </pre>
     *
     * @param str Ҫת�����ַ�
     *
     * @return �ַ������ַ�<code>""</code>
     */
    public static String defaultIfNull(String str) {
        return (str == null) ? EMPTY_STRING
                             : str;
    }

    /**
     * ����ַ���<code>null</code>���򷵻�ָ��Ĭ���ַ����򷵻��ַ��?
     * <pre>
     * StringUtil.defaultIfNull(null, "default")  = "default"
     * StringUtil.defaultIfNull("", "default")    = ""
     * StringUtil.defaultIfNull("  ", "default")  = "  "
     * StringUtil.defaultIfNull("bat", "default") = "bat"
     * </pre>
     *
     * @param str Ҫת�����ַ�
     * @param defaultStr Ĭ���ַ�
     *
     * @return �ַ����ָ����Ĭ���ַ�
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return (str == null) ? defaultStr
                             : str;
    }

    /**
     * ����ַ���<code>null</code>����ַ�<code>""</code>���򷵻ؿ��ַ�<code>""</code>�����򷵻��ַ��?
     * 
     * <p>
     * �˷���ʵ���Ϻ�<code>defaultIfNull(String)</code>��Ч��
     * <pre>
     * StringUtil.defaultIfEmpty(null)  = ""
     * StringUtil.defaultIfEmpty("")    = ""
     * StringUtil.defaultIfEmpty("  ")  = "  "
     * StringUtil.defaultIfEmpty("bat") = "bat"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return �ַ������ַ�<code>""</code>
     */
    public static String defaultIfEmpty(String str) {
        return (str == null) ? EMPTY_STRING
                             : str;
    }

    /**
     * ����ַ���<code>null</code>����ַ�<code>""</code>���򷵻�ָ��Ĭ���ַ����򷵻��ַ��?
     * <pre>
     * StringUtil.defaultIfEmpty(null, "default")  = "default"
     * StringUtil.defaultIfEmpty("", "default")    = "default"
     * StringUtil.defaultIfEmpty("  ", "default")  = "  "
     * StringUtil.defaultIfEmpty("bat", "default") = "bat"
     * </pre>
     *
     * @param str Ҫת�����ַ�
     * @param defaultStr Ĭ���ַ�
     *
     * @return �ַ����ָ����Ĭ���ַ�
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return ((str == null) || (str.length() == 0)) ? defaultStr
                                                      : str;
    }

    /**
     * ����ַ��ǿհף�<code>null</code>�����ַ�<code>""</code>��ֻ�пհ��ַ��򷵻ؿ��ַ�<code>""</code>�����򷵻��ַ��?
     * <pre>
     * StringUtil.defaultIfBlank(null)  = ""
     * StringUtil.defaultIfBlank("")    = ""
     * StringUtil.defaultIfBlank("  ")  = ""
     * StringUtil.defaultIfBlank("bat") = "bat"
     * </pre>
     *
     * @param str Ҫת�����ַ�
     *
     * @return �ַ������ַ�<code>""</code>
     */
    public static String defaultIfBlank(String str) {
        return isBlank(str) ? EMPTY_STRING
                            : str;
    }

    /**
     * ����ַ���<code>null</code>����ַ�<code>""</code>���򷵻�ָ��Ĭ���ַ����򷵻��ַ��?
     * <pre>
     * StringUtil.defaultIfBlank(null, "default")  = "default"
     * StringUtil.defaultIfBlank("", "default")    = "default"
     * StringUtil.defaultIfBlank("  ", "default")  = "default"
     * StringUtil.defaultIfBlank("bat", "default") = "bat"
     * </pre>
     *
     * @param str Ҫת�����ַ�
     * @param defaultStr Ĭ���ַ�
     *
     * @return �ַ����ָ����Ĭ���ַ�
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr
                            : str;
    }

    /* ============================================================================ */
    /*  ȥ�հף���ָ���ַ�ĺ���                                                */
    /*                                                                              */
    /*  ���·���������ȥһ���ִ��еĿհ׻�ָ���ַ�                                */
    /* ============================================================================ */

    /**
     * ��ȥ�ַ�ͷβ���Ŀհף�����ַ���<code>null</code>����Ȼ����<code>null</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trim(null)          = null
     * StringUtil.trim("")            = ""
     * StringUtil.trim("     ")       = ""
     * StringUtil.trim("abc")         = "abc"
     * StringUtil.trim("    abc    ") = "abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String trim(String str) {
        return trim(str, null, 0);
    }

    /**
     * ��ȥ�ַ�ͷβ����ָ���ַ�����ַ���<code>null</code>����Ȼ����<code>null</code>��
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim("", *)            = ""
     * StringUtil.trim("abc", null)      = "abc"
     * StringUtil.trim("  abc", null)    = "abc"
     * StringUtil.trim("abc  ", null)    = "abc"
     * StringUtil.trim(" abc ", null)    = "abc"
     * StringUtil.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param stripChars Ҫ��ȥ���ַ����Ϊ<code>null</code>��ʾ��ȥ�հ��ַ�
     *
     * @return ��ȥָ���ַ��ĵ��ַ����ԭ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    /**
     * ��ȥ�ַ�ͷ���Ŀհף�����ַ���<code>null</code>���򷵻�<code>null</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trimStart(null)         = null
     * StringUtil.trimStart("")           = ""
     * StringUtil.trimStart("abc")        = "abc"
     * StringUtil.trimStart("  abc")      = "abc"
     * StringUtil.trimStart("abc  ")      = "abc  "
     * StringUtil.trimStart(" abc ")      = "abc "
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimStart(String str) {
        return trim(str, null, -1);
    }

    /**
     * ��ȥ�ַ�ͷ����ָ���ַ�����ַ���<code>null</code>����Ȼ����<code>null</code>��
     * <pre>
     * StringUtil.trimStart(null, *)          = null
     * StringUtil.trimStart("", *)            = ""
     * StringUtil.trimStart("abc", "")        = "abc"
     * StringUtil.trimStart("abc", null)      = "abc"
     * StringUtil.trimStart("  abc", null)    = "abc"
     * StringUtil.trimStart("abc  ", null)    = "abc  "
     * StringUtil.trimStart(" abc ", null)    = "abc "
     * StringUtil.trimStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param stripChars Ҫ��ȥ���ַ����Ϊ<code>null</code>��ʾ��ȥ�հ��ַ�
     *
     * @return ��ȥָ���ַ��ĵ��ַ����ԭ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }

    /**
     * ��ȥ�ַ�β���Ŀհף�����ַ���<code>null</code>���򷵻�<code>null</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trimEnd(null)       = null
     * StringUtil.trimEnd("")         = ""
     * StringUtil.trimEnd("abc")      = "abc"
     * StringUtil.trimEnd("  abc")    = "  abc"
     * StringUtil.trimEnd("abc  ")    = "abc"
     * StringUtil.trimEnd(" abc ")    = " abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimEnd(String str) {
        return trim(str, null, 1);
    }

    /**
     * ��ȥ�ַ�β����ָ���ַ�����ַ���<code>null</code>����Ȼ����<code>null</code>��
     * <pre>
     * StringUtil.trimEnd(null, *)          = null
     * StringUtil.trimEnd("", *)            = ""
     * StringUtil.trimEnd("abc", "")        = "abc"
     * StringUtil.trimEnd("abc", null)      = "abc"
     * StringUtil.trimEnd("  abc", null)    = "  abc"
     * StringUtil.trimEnd("abc  ", null)    = "abc"
     * StringUtil.trimEnd(" abc ", null)    = " abc"
     * StringUtil.trimEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param stripChars Ҫ��ȥ���ַ����Ϊ<code>null</code>��ʾ��ȥ�հ��ַ�
     *
     * @return ��ȥָ���ַ��ĵ��ַ����ԭ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String trimEnd(String str, String stripChars) {
        return trim(str, stripChars, 1);
    }

    /**
     * ��ȥ�ַ�ͷβ���Ŀհף�������ַ��ǿ��ַ�<code>""</code>���򷵻�<code>null</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trimToNull(null)          = null
     * StringUtil.trimToNull("")            = null
     * StringUtil.trimToNull("     ")       = null
     * StringUtil.trimToNull("abc")         = "abc"
     * StringUtil.trimToNull("    abc    ") = "abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimToNull(String str) {
        return trimToNull(str, null);
    }

    /**
     * ��ȥ�ַ�ͷβ���Ŀհף�������ַ��ǿ��ַ�<code>""</code>���򷵻�<code>null</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim("", *)            = null
     * StringUtil.trim("abc", null)      = "abc"
     * StringUtil.trim("  abc", null)    = "abc"
     * StringUtil.trim("abc  ", null)    = "abc"
     * StringUtil.trim(" abc ", null)    = "abc"
     * StringUtil.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     * @param stripChars Ҫ��ȥ���ַ����Ϊ<code>null</code>��ʾ��ȥ�հ��ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimToNull(String str, String stripChars) {
        String result = trim(str, stripChars);

        if ((result == null) || (result.length() == 0)) {
            return null;
        }

        return result;
    }

    /**
     * ��ȥ�ַ�ͷβ���Ŀհף�����ַ���<code>null</code>���򷵻ؿ��ַ�<code>""</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trimToEmpty(null)          = ""
     * StringUtil.trimToEmpty("")            = ""
     * StringUtil.trimToEmpty("     ")       = ""
     * StringUtil.trimToEmpty("abc")         = "abc"
     * StringUtil.trimToEmpty("    abc    ") = "abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimToEmpty(String str) {
        return trimToEmpty(str, null);
    }

    /**
     * ��ȥ�ַ�ͷβ���Ŀհף�����ַ���<code>null</code>���򷵻ؿ��ַ�<code>""</code>��
     * 
     * <p>
     * ע�⣬��<code>String.trim</code>��ͬ���˷���ʹ��<code>Character.isWhitespace</code>���ж��հף�
     * �����Գ�ȥӢ���ַ�֮�������հף������Ŀո�
     * <pre>
     * StringUtil.trim(null, *)          = ""
     * StringUtil.trim("", *)            = ""
     * StringUtil.trim("abc", null)      = "abc"
     * StringUtil.trim("  abc", null)    = "abc"
     * StringUtil.trim("abc  ", null)    = "abc"
     * StringUtil.trim(" abc ", null)    = "abc"
     * StringUtil.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ��ȥ�հ׵��ַ����ԭ�ִ�Ϊ<code>null</code>�����ַ�Ϊ<code>""</code>���򷵻�<code>null</code>
     */
    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);

        if (result == null) {
            return EMPTY_STRING;
        }

        return result;
    }

    /**
     * ��ȥ�ַ�ͷβ����ָ���ַ�����ַ���<code>null</code>����Ȼ����<code>null</code>��
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim("", *)            = ""
     * StringUtil.trim("abc", null)      = "abc"
     * StringUtil.trim("  abc", null)    = "abc"
     * StringUtil.trim("abc  ", null)    = "abc"
     * StringUtil.trim(" abc ", null)    = "abc"
     * StringUtil.trim("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param stripChars Ҫ��ȥ���ַ����Ϊ<code>null</code>��ʾ��ȥ�հ��ַ�
     * @param mode <code>-1</code>��ʾtrimStart��<code>0</code>��ʾtrimȫ����<code>1</code>��ʾtrimEnd
     *
     * @return ��ȥָ���ַ��ĵ��ַ����ԭ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end   = length;

        // ɨ���ַ�ͷ��
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }

        // ɨ���ַ�β��
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    /* ============================================================================ */
    /*  �ȽϺ���                                                                  */
    /*                                                                              */
    /*  ���·��������Ƚ������ַ��Ƿ���ͬ��                                        */
    /* ============================================================================ */

    /**
     * �Ƚ������ַ���Сд���У���
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, "abc")  = false
     * StringUtil.equals("abc", null)  = false
     * StringUtil.equals("abc", "abc") = true
     * StringUtil.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 Ҫ�Ƚϵ��ַ�1
     * @param str2 Ҫ�Ƚϵ��ַ�2
     *
     * @return ��������ַ���ͬ�����߶���<code>null</code>���򷵻�<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * �Ƚ������ַ���Сд�����У���
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, "abc")  = false
     * StringUtil.equalsIgnoreCase("abc", null)  = false
     * StringUtil.equalsIgnoreCase("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 Ҫ�Ƚϵ��ַ�1
     * @param str2 Ҫ�Ƚϵ��ַ�2
     *
     * @return ��������ַ���ͬ�����߶���<code>null</code>���򷵻�<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /* ============================================================================ */
    /*  �ַ������ж�����                                                        */
    /*                                                                              */
    /*  �ж��ַ�������Ƿ�Ϊ����ĸ�����֡��հ׵�                                  */
    /* ============================================================================ */

    /**
     * �ж��ַ��Ƿ�ֻ��unicode��ĸ��
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isAlpha(null)   = false
     * StringUtil.isAlpha("")     = true
     * StringUtil.isAlpha("  ")   = false
     * StringUtil.isAlpha("abc")  = true
     * StringUtil.isAlpha("ab2c") = false
     * StringUtil.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode��ĸ��ɣ��򷵻�<code>true</code>
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode��ĸ�Ϳո�<code>' '</code>��
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isAlphaSpace(null)   = false
     * StringUtil.isAlphaSpace("")     = true
     * StringUtil.isAlphaSpace("  ")   = true
     * StringUtil.isAlphaSpace("abc")  = true
     * StringUtil.isAlphaSpace("ab c") = true
     * StringUtil.isAlphaSpace("ab2c") = false
     * StringUtil.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode��ĸ�Ϳո���ɣ��򷵻�<code>true</code>
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode��ĸ�����֡�
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isAlphanumeric(null)   = false
     * StringUtil.isAlphanumeric("")     = true
     * StringUtil.isAlphanumeric("  ")   = false
     * StringUtil.isAlphanumeric("abc")  = true
     * StringUtil.isAlphanumeric("ab c") = false
     * StringUtil.isAlphanumeric("ab2c") = true
     * StringUtil.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode��ĸ������ɣ��򷵻�<code>true</code>
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode��ĸ���ֺͿո�<code>' '</code>��
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isAlphanumericSpace(null)   = false
     * StringUtil.isAlphanumericSpace("")     = true
     * StringUtil.isAlphanumericSpace("  ")   = true
     * StringUtil.isAlphanumericSpace("abc")  = true
     * StringUtil.isAlphanumericSpace("ab c") = true
     * StringUtil.isAlphanumericSpace("ab2c") = true
     * StringUtil.isAlphanumericSpace("ab-c") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode��ĸ���ֺͿո���ɣ��򷵻�<code>true</code>
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode���֡�
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isNumeric(null)   = false
     * StringUtil.isNumeric("")     = true
     * StringUtil.isNumeric("  ")   = false
     * StringUtil.isNumeric("123")  = true
     * StringUtil.isNumeric("12 3") = false
     * StringUtil.isNumeric("ab2c") = false
     * StringUtil.isNumeric("12-3") = false
     * StringUtil.isNumeric("12.3") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode������ɣ��򷵻�<code>true</code>
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode���ֺͿո�<code>' '</code>��
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isNumericSpace(null)   = false
     * StringUtil.isNumericSpace("")     = true
     * StringUtil.isNumericSpace("  ")   = true
     * StringUtil.isNumericSpace("123")  = true
     * StringUtil.isNumericSpace("12 3") = true
     * StringUtil.isNumericSpace("ab2c") = false
     * StringUtil.isNumericSpace("12-3") = false
     * StringUtil.isNumericSpace("12.3") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode���ֺͿո���ɣ��򷵻�<code>true</code>
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * �ж��ַ��Ƿ�ֻ��unicode�հס�
     * 
     * <p>
     * <code>null</code>������<code>false</code>�����ַ�<code>""</code>������<code>true</code>��
     * </p>
     * <pre>
     * StringUtil.isWhitespace(null)   = false
     * StringUtil.isWhitespace("")     = true
     * StringUtil.isWhitespace("  ")   = true
     * StringUtil.isWhitespace("abc")  = false
     * StringUtil.isWhitespace("ab2c") = false
     * StringUtil.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str Ҫ�����ַ�
     *
     * @return ����ַ��<code>null</code>����ȫ��unicode�հ���ɣ��򷵻�<code>true</code>
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /* ============================================================================ */
    /*  ��Сдת����                                                                */
    /* ============================================================================ */

    /**
     * ���ַ�ת���ɴ�д��
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toUpperCase(null)  = null
     * StringUtil.toUpperCase("")    = ""
     * StringUtil.toUpperCase("aBc") = "ABC"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return ��д�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toUpperCase();
    }

    /**
     * ���ַ�ת����Сд��
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toLowerCase(null)  = null
     * StringUtil.toLowerCase("")    = ""
     * StringUtil.toLowerCase("aBc") = "abc"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return ��д�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    /**
     * ���ַ�����ַ�ת�ɴ�д��<code>Character.toTitleCase</code>���������ַ�䡣
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.capitalize(null)  = null
     * StringUtil.capitalize("")    = ""
     * StringUtil.capitalize("cat") = "Cat"
     * StringUtil.capitalize("cAt") = "CAt"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return ���ַ�Ϊ��д���ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String capitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0)))
                                       .append(str.substring(1)).toString();
    }

    /**
     * ���ַ�����ַ�ת��Сд�������ַ�䡣
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.uncapitalize(null)  = null
     * StringUtil.uncapitalize("")    = ""
     * StringUtil.uncapitalize("Cat") = "cat"
     * StringUtil.uncapitalize("CAT") = "cAT"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return ���ַ�ΪСд���ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String uncapitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0)))
                                       .append(str.substring(1)).toString();
    }

    /**
     * ��ת�ַ�Ĵ�Сд��
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.swapCase(null)                 = null
     * StringUtil.swapCase("")                   = ""
     * StringUtil.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return ��Сд����ת���ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String swapCase(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        StringBuffer buffer = new StringBuffer(strLen);

        char         ch = 0;

        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }

            buffer.append(ch);
        }

        return buffer.toString();
    }

    /**
     * ���ַ�ת����camel case��
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toCamelCase(null)  = null
     * StringUtil.toCamelCase("")    = ""
     * StringUtil.toCamelCase("aBc") = "aBc"
     * StringUtil.toCamelCase("aBc def") = "aBcDef"
     * StringUtil.toCamelCase("aBc def_ghi") = "aBcDefGhi"
     * StringUtil.toCamelCase("aBc def_ghi 123") = "aBcDefGhi123"
     * </pre>
     * </p>
     * 
     * <p>
     * �˷����ᱣ�������»��ߺͿհ���������зָ���
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return camel case�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toCamelCase(String str) {
        return CAMEL_CASE_TOKENIZER.parse(str);
    }

    /**
     * ���ַ�ת����pascal case��
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toPascalCase(null)  = null
     * StringUtil.toPascalCase("")    = ""
     * StringUtil.toPascalCase("aBc") = "ABc"
     * StringUtil.toPascalCase("aBc def") = "ABcDef"
     * StringUtil.toPascalCase("aBc def_ghi") = "ABcDefGhi"
     * StringUtil.toPascalCase("aBc def_ghi 123") = "aBcDefGhi123"
     * </pre>
     * </p>
     * 
     * <p>
     * �˷����ᱣ�������»��ߺͿհ���������зָ���
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return pascal case�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toPascalCase(String str) {
        return PASCAL_CASE_TOKENIZER.parse(str);
    }

    /**
     * ���ַ�ת�����»��߷ָ��Ĵ�д�ַ�
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toUpperCaseWithUnderscores(null)  = null
     * StringUtil.toUpperCaseWithUnderscores("")    = ""
     * StringUtil.toUpperCaseWithUnderscores("aBc") = "A_BC"
     * StringUtil.toUpperCaseWithUnderscores("aBc def") = "A_BC_DEF"
     * StringUtil.toUpperCaseWithUnderscores("aBc def_ghi") = "A_BC_DEF_GHI"
     * StringUtil.toUpperCaseWithUnderscores("aBc def_ghi 123") = "A_BC_DEF_GHI_123"
     * StringUtil.toUpperCaseWithUnderscores("__a__Bc__") = "__A__BC__"
     * </pre>
     * </p>
     * 
     * <p>
     * �˷����ᱣ�����˿հ���������зָ���
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return �»��߷ָ��Ĵ�д�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toUpperCaseWithUnderscores(String str) {
        return UPPER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
    }

    /**
     * ���ַ�ת�����»��߷ָ���Сд�ַ�
     * 
     * <p>
     * ����ַ���<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.toLowerCaseWithUnderscores(null)  = null
     * StringUtil.toLowerCaseWithUnderscores("")    = ""
     * StringUtil.toLowerCaseWithUnderscores("aBc") = "a_bc"
     * StringUtil.toLowerCaseWithUnderscores("aBc def") = "a_bc_def"
     * StringUtil.toLowerCaseWithUnderscores("aBc def_ghi") = "a_bc_def_ghi"
     * StringUtil.toLowerCaseWithUnderscores("aBc def_ghi 123") = "a_bc_def_ghi_123"
     * StringUtil.toLowerCaseWithUnderscores("__a__Bc__") = "__a__bc__"
     * </pre>
     * </p>
     * 
     * <p>
     * �˷����ᱣ�����˿հ���������зָ���
     * </p>
     *
     * @param str Ҫת�����ַ�
     *
     * @return �»��߷ָ���Сд�ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String toLowerCaseWithUnderscores(String str) {
        return LOWER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
    }

    /** �������ʵĽ������� */
    private static final WordTokenizer CAMEL_CASE_TOKENIZER = new WordTokenizer() {
            protected void startSentence(StringBuffer buffer, char ch) {
                buffer.append(Character.toLowerCase(ch));
            }

            protected void startWord(StringBuffer buffer, char ch) {
                if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                    buffer.append(Character.toUpperCase(ch));
                } else {
                    buffer.append(Character.toLowerCase(ch));
                }
            }

            protected void inWord(StringBuffer buffer, char ch) {
                buffer.append(Character.toLowerCase(ch));
            }

            protected void startDigitSentence(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void startDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDelimiter(StringBuffer buffer, char ch) {
                if (ch != UNDERSCORE) {
                    buffer.append(ch);
                }
            }
        };

    private static final WordTokenizer PASCAL_CASE_TOKENIZER = new WordTokenizer() {
            protected void startSentence(StringBuffer buffer, char ch) {
                buffer.append(Character.toUpperCase(ch));
            }

            protected void startWord(StringBuffer buffer, char ch) {
                buffer.append(Character.toUpperCase(ch));
            }

            protected void inWord(StringBuffer buffer, char ch) {
                buffer.append(Character.toLowerCase(ch));
            }

            protected void startDigitSentence(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void startDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDelimiter(StringBuffer buffer, char ch) {
                if (ch != UNDERSCORE) {
                    buffer.append(ch);
                }
            }
        };

    private static final WordTokenizer UPPER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {
            protected void startSentence(StringBuffer buffer, char ch) {
                buffer.append(Character.toUpperCase(ch));
            }

            protected void startWord(StringBuffer buffer, char ch) {
                if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                    buffer.append(UNDERSCORE);
                }

                buffer.append(Character.toUpperCase(ch));
            }

            protected void inWord(StringBuffer buffer, char ch) {
                buffer.append(Character.toUpperCase(ch));
            }

            protected void startDigitSentence(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void startDigitWord(StringBuffer buffer, char ch) {
                if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                    buffer.append(UNDERSCORE);
                }

                buffer.append(ch);
            }

            protected void inDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDelimiter(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }
        };

    private static final WordTokenizer LOWER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {
            protected void startSentence(StringBuffer buffer, char ch) {
                buffer.append(Character.toLowerCase(ch));
            }

            protected void startWord(StringBuffer buffer, char ch) {
                if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                    buffer.append(UNDERSCORE);
                }

                buffer.append(Character.toLowerCase(ch));
            }

            protected void inWord(StringBuffer buffer, char ch) {
                buffer.append(Character.toLowerCase(ch));
            }

            protected void startDigitSentence(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void startDigitWord(StringBuffer buffer, char ch) {
                if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                    buffer.append(UNDERSCORE);
                }

                buffer.append(ch);
            }

            protected void inDigitWord(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }

            protected void inDelimiter(StringBuffer buffer, char ch) {
                buffer.append(ch);
            }
        };

    /**
     * �����������﷨��ɵ�<code>SENTENCE</code>��
     * <pre>
     *  SENTENCE = WORD (DELIMITER* WORD)*
     * 
     *  WORD = UPPER_CASE_WORD | LOWER_CASE_WORD | TITLE_CASE_WORD | DIGIT_WORD
     * 
     *  UPPER_CASE_WORD = UPPER_CASE_LETTER+
     *  LOWER_CASE_WORD = LOWER_CASE_LETTER+
     *  TITLE_CASE_WORD = UPPER_CASE_LETTER LOWER_CASE_LETTER+
     *  DIGIT_WORD      = DIGIT+
     * 
     *  UPPER_CASE_LETTER = Character.isUpperCase()
     *  LOWER_CASE_LETTER = Character.isLowerCase()
     *  DIGIT             = Character.isDigit()
     *  NON_LETTER_DIGIT  = !Character.isUpperCase() && !Character.isLowerCase() && !Character.isDigit()
     * 
     *  DELIMITER = WHITESPACE | NON_LETTER_DIGIT
     * </pre>
     */
    private abstract static class WordTokenizer {
        protected static final char UNDERSCORE = '_';

        /**
         * Parse sentence��
         */
        public String parse(String str) {
            if (StringUtil.isEmpty(str)) {
                return str;
            }

            int          length = str.length();
            StringBuffer buffer = new StringBuffer(length);

            for (int index = 0; index < length; index++) {
                char ch = str.charAt(index);

                // ���Կհס�
                if (Character.isWhitespace(ch)) {
                    continue;
                }

                // ��д��ĸ��ʼ��UpperCaseWord����TitleCaseWord��
                if (Character.isUpperCase(ch)) {
                    int wordIndex = index + 1;

                    while (wordIndex < length) {
                        char wordChar = str.charAt(wordIndex);

                        if (Character.isUpperCase(wordChar)) {
                            wordIndex++;
                        } else if (Character.isLowerCase(wordChar)) {
                            wordIndex--;
                            break;
                        } else {
                            break;
                        }
                    }

                    // 1. wordIndex == length��˵�����һ����ĸΪ��д����upperCaseWord����֮��
                    // 2. wordIndex == index��˵��index��Ϊһ��titleCaseWord��
                    // 3. wordIndex > index��˵��index��wordIndex - 1��ȫ���Ǵ�д����upperCaseWord���?
                    if ((wordIndex == length) || (wordIndex > index)) {
                        index = parseUpperCaseWord(buffer, str, index, wordIndex);
                    } else {
                        index = parseTitleCaseWord(buffer, str, index);
                    }

                    continue;
                }

                // Сд��ĸ��ʼ��LowerCaseWord��
                if (Character.isLowerCase(ch)) {
                    index = parseLowerCaseWord(buffer, str, index);
                    continue;
                }

                // ���ֿ�ʼ��DigitWord��
                if (Character.isDigit(ch)) {
                    index = parseDigitWord(buffer, str, index);
                    continue;
                }

                // ����ĸ���ֿ�ʼ��Delimiter�� 
                inDelimiter(buffer, ch);
            }

            return buffer.toString();
        }

        private int parseUpperCaseWord(StringBuffer buffer, String str, int index, int length) {
            char ch = str.charAt(index++);

            // ����ĸ����Ȼ������Ϊ��д��
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // ������ĸ����ΪСд��
            for (; index < length; index++) {
                ch = str.charAt(index);
                inWord(buffer, ch);
            }

            return index - 1;
        }

        private int parseLowerCaseWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // ����ĸ����Ȼ������ΪСд��
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // ������ĸ����ΪСд��
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isLowerCase(ch)) {
                    inWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseTitleCaseWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // ����ĸ����Ȼ������Ϊ��д��
            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            // ������ĸ����ΪСд��
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isLowerCase(ch)) {
                    inWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        private int parseDigitWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            // ���ַ��Ȼ������Ϊ���֡�
            if (buffer.length() == 0) {
                startDigitSentence(buffer, ch);
            } else {
                startDigitWord(buffer, ch);
            }

            // �����ַ��Ϊ���֡�
            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (Character.isDigit(ch)) {
                    inDigitWord(buffer, ch);
                } else {
                    break;
                }
            }

            return index - 1;
        }

        protected boolean isDelimiter(char ch) {
            return !Character.isUpperCase(ch) && !Character.isLowerCase(ch)
            && !Character.isDigit(ch);
        }

        protected abstract void startSentence(StringBuffer buffer, char ch);

        protected abstract void startWord(StringBuffer buffer, char ch);

        protected abstract void inWord(StringBuffer buffer, char ch);

        protected abstract void startDigitSentence(StringBuffer buffer, char ch);

        protected abstract void startDigitWord(StringBuffer buffer, char ch);

        protected abstract void inDigitWord(StringBuffer buffer, char ch);

        protected abstract void inDelimiter(StringBuffer buffer, char ch);
    }

    /* ============================================================================ */
    /*  �ַ����Ӻ���                                                            */
    /*                                                                              */
    /*  ���������ָ���ָ������ӳ��ַ�                                        */
    /* ============================================================================ */

    /**
     * �������е�Ԫ�����ӳ�һ���ַ�
     * <pre>
     * StringUtil.join(null)            = null
     * StringUtil.join([])              = ""
     * StringUtil.join([null])          = ""
     * StringUtil.join(["a", "b", "c"]) = "abc"
     * StringUtil.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array Ҫ���ӵ�����
     *
     * @return ���Ӻ���ַ����ԭ����Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * �������е�Ԫ�����ӳ�һ���ַ�
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtil.join(["a", "b", "c"], null) = "abc"
     * StringUtil.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array Ҫ���ӵ�����
     * @param separator �ָ���
     *
     * @return ���Ӻ���ַ����ԭ����Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }

        int arraySize = array.length;
        int bufSize = (arraySize == 0) ? 0
                                       : ((((array[0] == null) ? 16
                                                               : array[0].toString().length()) + 1) * arraySize);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }

            if (array[i] != null) {
                buf.append(array[i]);
            }
        }

        return buf.toString();
    }

    /**
     * �������е�Ԫ�����ӳ�һ���ַ�
     * <pre>
     * StringUtil.join(null, *)                = null
     * StringUtil.join([], *)                  = ""
     * StringUtil.join([null], *)              = ""
     * StringUtil.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], null)  = "abc"
     * StringUtil.join(["a", "b", "c"], "")    = "abc"
     * StringUtil.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array Ҫ���ӵ�����
     * @param separator �ָ���
     *
     * @return ���Ӻ���ַ����ԭ����Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }

        if (separator == null) {
            separator = EMPTY_STRING;
        }

        int arraySize = array.length;

        // ArraySize ==  0: Len = 0
        // ArraySize > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (���ƴ�Լ���е��ַ�һ��)
        int bufSize = (arraySize == 0) ? 0
                                       : (arraySize * (((array[0] == null) ? 16
                                                                           : array[0].toString()
                                                                                     .length())
            + ((separator != null) ? separator.length()
                                   : 0)));

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if ((separator != null) && (i > 0)) {
                buf.append(separator);
            }

            if (array[i] != null) {
                buf.append(array[i]);
            }
        }

        return buf.toString();
    }

    /**
     * ��<code>Iterator</code>�е�Ԫ�����ӳ�һ���ַ�
     * <pre>
     * StringUtil.join(null, *)                = null
     * StringUtil.join([], *)                  = ""
     * StringUtil.join([null], *)              = ""
     * StringUtil.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], null)  = "abc"
     * StringUtil.join(["a", "b", "c"], "")    = "abc"
     * StringUtil.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param iterator Ҫ���ӵ�<code>Iterator</code>
     * @param separator �ָ���
     *
     * @return ���Ӻ���ַ����ԭ����Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(256); // JavaĬ��ֵ��16, ����ƫС

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj != null) {
                buf.append(obj);
            }

            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }

        return buf.toString();
    }

    /**
     * ��<code>Iterator</code>�е�Ԫ�����ӳ�һ���ַ�
     * <pre>
     * StringUtil.join(null, *)                = null
     * StringUtil.join([], *)                  = ""
     * StringUtil.join([null], *)              = ""
     * StringUtil.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], null)  = "abc"
     * StringUtil.join(["a", "b", "c"], "")    = "abc"
     * StringUtil.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param iterator Ҫ���ӵ�<code>Iterator</code>
     * @param separator �ָ���
     *
     * @return ���Ӻ���ַ����ԭ����Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(256); // JavaĬ��ֵ��16, ����ƫС

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj != null) {
                buf.append(obj);
            }

            if ((separator != null) && iterator.hasNext()) {
                buf.append(separator);
            }
        }

        return buf.toString();
    }

    /* ============================================================================ */
    /*  �ַ���Һ��� ���� �ַ���ַ�                                          */
    /*                                                                              */
    /*  ���ַ��в���ָ���ַ���ַ�                                            */
    /* ============================================================================ */

    /**
     * ���ַ��в���ָ���ַ����ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.indexOf(null, *)         = -1
     * StringUtil.indexOf("", *)           = -1
     * StringUtil.indexOf("aabaabaa", 'a') = 0
     * StringUtil.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ���ҵ��ַ�
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOf(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.indexOf(searchChar);
    }

    /**
     * ���ַ��в���ָ���ַ����ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.indexOf(null, *, *)          = -1
     * StringUtil.indexOf("", *, *)            = -1
     * StringUtil.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtil.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtil.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtil.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ���ҵ��ַ�
     * @param startPos ��ʼ����������ֵ�����С��0������0
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.indexOf(searchChar, startPos);
    }

    /**
     * ���ַ��в���ָ���ַ������ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.indexOf(null, *)          = -1
     * StringUtil.indexOf(*, null)          = -1
     * StringUtil.indexOf("", "")           = 0
     * StringUtil.indexOf("aabaabaa", "a")  = 0
     * StringUtil.indexOf("aabaabaa", "b")  = 2
     * StringUtil.indexOf("aabaabaa", "ab") = 1
     * StringUtil.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStr Ҫ���ҵ��ַ�
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.indexOf(searchStr);
    }

    /**
     * ���ַ��в���ָ���ַ������ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.indexOf(null, *, *)          = -1
     * StringUtil.indexOf(*, null, *)          = -1
     * StringUtil.indexOf("", "", 0)           = 0
     * StringUtil.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtil.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtil.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtil.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtil.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtil.indexOf("aabaabaa", "b", -1) = 2
     * StringUtil.indexOf("aabaabaa", "", 2)   = 2
     * StringUtil.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStr Ҫ���ҵ��ַ�
     * @param startPos ��ʼ����������ֵ�����С��0������0
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        // JDK1.3�����°汾��bug��������ȷ������������
        if ((searchStr.length() == 0) && (startPos >= str.length())) {
            return str.length();
        }

        return str.indexOf(searchStr, startPos);
    }

    /**
     * ���ַ��в���ָ���ַ���е��ַ����ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ��Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * <pre>
     * StringUtil.indexOfAny(null, *)                = -1
     * StringUtil.indexOfAny("", *)                  = -1
     * StringUtil.indexOfAny(*, null)                = -1
     * StringUtil.indexOfAny(*, [])                  = -1
     * StringUtil.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringUtil.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringUtil.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChars Ҫ�������ַ��
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOfAny(String str, char[] searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                    || (searchChars.length == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * ���ַ��в���ָ���ַ���е��ַ����ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ��Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * <pre>
     * StringUtil.indexOfAny(null, *)            = -1
     * StringUtil.indexOfAny("", *)              = -1
     * StringUtil.indexOfAny(*, null)            = -1
     * StringUtil.indexOfAny(*, "")              = -1
     * StringUtil.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtil.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtil.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChars Ҫ�������ַ��
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOfAny(String str, String searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                    || (searchChars.length() == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length(); j++) {
                if (searchChars.charAt(j) == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * ���ַ��в���ָ���ַ����е��ַ������ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ���Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * ����ַ��ϰ���<code>""</code>�������ַ�Ϊ<code>null</code>���򷵻�<code>str.length()</code>
     * <pre>
     * StringUtil.indexOfAny(null, *)                     = -1
     * StringUtil.indexOfAny(*, null)                     = -1
     * StringUtil.indexOfAny(*, [])                       = -1
     * StringUtil.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringUtil.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringUtil.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringUtil.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringUtil.indexOfAny("zzabyycdxx", [""])          = 0
     * StringUtil.indexOfAny("", [""])                    = 0
     * StringUtil.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStrs Ҫ�������ַ���
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;

        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }

            tmp = str.indexOf(search);

            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? (-1)
                                          : ret;
    }

    /**
     * ���ַ��в��Ҳ���ָ���ַ���е��ַ����ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ��Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * <pre>
     * StringUtil.indexOfAnyBut(null, *)             = -1
     * StringUtil.indexOfAnyBut("", *)               = -1
     * StringUtil.indexOfAnyBut(*, null)             = -1
     * StringUtil.indexOfAnyBut(*, [])               = -1
     * StringUtil.indexOfAnyBut("zzabyycdxx",'za')   = 3
     * StringUtil.indexOfAnyBut("zzabyycdxx", 'by')  = 0
     * StringUtil.indexOfAnyBut("aba", 'ab')         = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChars Ҫ�������ַ��
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOfAnyBut(String str, char[] searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                    || (searchChars.length == 0)) {
            return -1;
        }

        outer: 
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    continue outer;
                }
            }

            return i;
        }

        return -1;
    }

    /**
     * ���ַ��в��Ҳ���ָ���ַ���е��ַ����ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ��Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * <pre>
     * StringUtil.indexOfAnyBut(null, *)            = -1
     * StringUtil.indexOfAnyBut("", *)              = -1
     * StringUtil.indexOfAnyBut(*, null)            = -1
     * StringUtil.indexOfAnyBut(*, "")              = -1
     * StringUtil.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringUtil.indexOfAnyBut("zzabyycdxx", "by") = 0
     * StringUtil.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChars Ҫ�������ַ��
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int indexOfAnyBut(String str, String searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                    || (searchChars.length() == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * ���ַ�β����ʼ����ָ���ַ����ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.lastIndexOf(null, *)         = -1
     * StringUtil.lastIndexOf("", *)           = -1
     * StringUtil.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtil.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ���ҵ��ַ�
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int lastIndexOf(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.lastIndexOf(searchChar);
    }

    /**
     * ���ַ�β����ʼ����ָ���ַ����ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.lastIndexOf(null, *, *)          = -1
     * StringUtil.lastIndexOf("", *,  *)           = -1
     * StringUtil.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtil.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtil.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtil.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtil.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtil.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ���ҵ��ַ�
     * @param startPos ��ָ������ʼ��ǰ����
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * ���ַ�β����ʼ����ָ���ַ������ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.lastIndexOf(null, *)         = -1
     * StringUtil.lastIndexOf("", *)           = -1
     * StringUtil.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtil.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStr Ҫ���ҵ��ַ�
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int lastIndexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.lastIndexOf(searchStr);
    }

    /**
     * ���ַ�β����ʼ����ָ���ַ������ص�һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>��
     * <pre>
     * StringUtil.lastIndexOf(null, *, *)          = -1
     * StringUtil.lastIndexOf(*, null, *)          = -1
     * StringUtil.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtil.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtil.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtil.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtil.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtil.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtil.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStr Ҫ���ҵ��ַ�
     * @param startPos ��ָ������ʼ��ǰ����
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.lastIndexOf(searchStr, startPos);
    }

    /**
     * ���ַ�β����ʼ����ָ���ַ����е��ַ������ص�һ��ƥ�����ʼ���� ����ַ�Ϊ<code>null</code>���򷵻�<code>-1</code>��
     * ����ַ���Ϊ<code>null</code>��գ�Ҳ����<code>-1</code>��
     * ����ַ��ϰ���<code>""</code>�������ַ�Ϊ<code>null</code>���򷵻�<code>str.length()</code>
     * <pre>
     * StringUtil.lastIndexOfAny(null, *)                   = -1
     * StringUtil.lastIndexOfAny(*, null)                   = -1
     * StringUtil.lastIndexOfAny(*, [])                     = -1
     * StringUtil.lastIndexOfAny(*, [null])                 = -1
     * StringUtil.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringUtil.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringUtil.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtil.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtil.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStrs Ҫ�������ַ���
     *
     * @return ��һ��ƥ�������ֵ������ַ�Ϊ<code>null</code>��δ�ҵ����򷵻�<code>-1</code>
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int searchStrsLength = searchStrs.length;
        int index = -1;
        int tmp   = 0;

        for (int i = 0; i < searchStrsLength; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }

            tmp = str.lastIndexOf(search);

            if (tmp > index) {
                index = tmp;
            }
        }

        return index;
    }

    /**
     * ����ַ����Ƿ��ָ�����ַ�����ַ�Ϊ<code>null</code>��������<code>false</code>��
     * <pre>
     * StringUtil.contains(null, *)    = false
     * StringUtil.contains("", *)      = false
     * StringUtil.contains("abc", 'a') = true
     * StringUtil.contains("abc", 'z') = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean contains(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }

        return str.indexOf(searchChar) >= 0;
    }

    /**
     * ����ַ����Ƿ��ָ�����ַ�����ַ�Ϊ<code>null</code>��������<code>false</code>��
     * <pre>
     * StringUtil.contains(null, *)     = false
     * StringUtil.contains(*, null)     = false
     * StringUtil.contains("", "")      = true
     * StringUtil.contains("abc", "")   = true
     * StringUtil.contains("abc", "a")  = true
     * StringUtil.contains("abc", "z")  = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param searchStr Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }

        return str.indexOf(searchStr) >= 0;
    }

    /**
     * ����ַ����Ƿ�ֻ��ָ���ַ���е��ַ�
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>false</code>��
     * ����ַ��Ϊ<code>null</code>�򷵻�<code>false</code>�� ���ǿ��ַ���Զ����<code>true</code>.
     * </p>
     * <pre>
     * StringUtil.containsOnly(null, *)       = false
     * StringUtil.containsOnly(*, null)       = false
     * StringUtil.containsOnly("", *)         = true
     * StringUtil.containsOnly("ab", '')      = false
     * StringUtil.containsOnly("abab", 'abc') = true
     * StringUtil.containsOnly("ab1", 'abc')  = false
     * StringUtil.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param valid Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean containsOnly(String str, char[] valid) {
        if ((valid == null) || (str == null)) {
            return false;
        }

        if (str.length() == 0) {
            return true;
        }

        if (valid.length == 0) {
            return false;
        }

        return indexOfAnyBut(str, valid) == -1;
    }

    /**
     * ����ַ����Ƿ�ֻ��ָ���ַ���е��ַ�
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>false</code>��
     * ����ַ��Ϊ<code>null</code>�򷵻�<code>false</code>�� ���ǿ��ַ���Զ����<code>true</code>.
     * </p>
     * <pre>
     * StringUtil.containsOnly(null, *)       = false
     * StringUtil.containsOnly(*, null)       = false
     * StringUtil.containsOnly("", *)         = true
     * StringUtil.containsOnly("ab", "")      = false
     * StringUtil.containsOnly("abab", "abc") = true
     * StringUtil.containsOnly("ab1", "abc")  = false
     * StringUtil.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param valid Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean containsOnly(String str, String valid) {
        if ((str == null) || (valid == null)) {
            return false;
        }

        return containsOnly(str, valid.toCharArray());
    }

    /**
     * ����ַ����Ƿ񲻰�ָ���ַ���е��ַ�
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>false</code>�� ����ַ��Ϊ<code>null</code>�򷵻�<code>true</code>��
     * ���ǿ��ַ���Զ����<code>true</code>.
     * </p>
     * <pre>
     * StringUtil.containsNone(null, *)       = true
     * StringUtil.containsNone(*, null)       = true
     * StringUtil.containsNone("", *)         = true
     * StringUtil.containsNone("ab", '')      = true
     * StringUtil.containsNone("abab", 'xyz') = true
     * StringUtil.containsNone("ab1", 'xyz')  = true
     * StringUtil.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param invalid Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean containsNone(String str, char[] invalid) {
        if ((str == null) || (invalid == null)) {
            return true;
        }

        int strSize   = str.length();
        int validSize = invalid.length;

        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < validSize; j++) {
                if (invalid[j] == ch) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * ����ַ����Ƿ񲻰�ָ���ַ���е��ַ�
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>false</code>�� ����ַ��Ϊ<code>null</code>�򷵻�<code>true</code>��
     * ���ǿ��ַ���Զ����<code>true</code>.
     * </p>
     * <pre>
     * StringUtil.containsNone(null, *)       = true
     * StringUtil.containsNone(*, null)       = true
     * StringUtil.containsNone("", *)         = true
     * StringUtil.containsNone("ab", "")      = true
     * StringUtil.containsNone("abab", "xyz") = true
     * StringUtil.containsNone("ab1", "xyz")  = true
     * StringUtil.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param str Ҫɨ����ַ�
     * @param invalidChars Ҫ���ҵ��ַ�
     *
     * @return ����ҵ����򷵻�<code>true</code>
     */
    public static boolean containsNone(String str, String invalidChars) {
        if ((str == null) || (invalidChars == null)) {
            return true;
        }

        return containsNone(str, invalidChars.toCharArray());
    }

    /**
     * ȡ��ָ���Ӵ����ַ��г��ֵĴ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>��գ��򷵻�<code>0</code>��
     * <pre>
     * StringUtil.countMatches(null, *)       = 0
     * StringUtil.countMatches("", *)         = 0
     * StringUtil.countMatches("abba", null)  = 0
     * StringUtil.countMatches("abba", "")    = 0
     * StringUtil.countMatches("abba", "a")   = 2
     * StringUtil.countMatches("abba", "ab")  = 1
     * StringUtil.countMatches("abba", "xxx") = 0
     * </pre>
     * </p>
     *
     * @param str Ҫɨ����ַ�
     * @param subStr ���ַ�
     *
     * @return �Ӵ����ַ��г��ֵĴ�������ַ�Ϊ<code>null</code>��գ��򷵻�<code>0</code>
     */
    public static int countMatches(String str, String subStr) {
        if ((str == null) || (str.length() == 0) || (subStr == null) || (subStr.length() == 0)) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }

        return count;
    }

    /* ============================================================================ */
    /*  ȡ�Ӵ�����                                                                */
    /* ============================================================================ */

    /**
     * ȡָ���ַ���Ӵ���
     * 
     * <p>
     * �����������β����ʼ���㡣����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * <pre>
     * StringUtil.substring(null, *)   = null
     * StringUtil.substring("", *)     = ""
     * StringUtil.substring("abc", 0)  = "abc"
     * StringUtil.substring("abc", 2)  = "c"
     * StringUtil.substring("abc", 4)  = ""
     * StringUtil.substring("abc", -2) = "bc"
     * StringUtil.substring("abc", -4) = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param start ��ʼ�������Ϊ�����ʾ��β������
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }

        if (start > str.length()) {
            return EMPTY_STRING;
        }

        return str.substring(start);
    }

    /**
     * ȡָ���ַ���Ӵ���
     * 
     * <p>
     * �����������β����ʼ���㡣����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * <pre>
     * StringUtil.substring(null, *, *)    = null
     * StringUtil.substring("", * ,  *)    = "";
     * StringUtil.substring("abc", 0, 2)   = "ab"
     * StringUtil.substring("abc", 2, 0)   = ""
     * StringUtil.substring("abc", 2, 4)   = "c"
     * StringUtil.substring("abc", 4, 6)   = ""
     * StringUtil.substring("abc", 2, 2)   = ""
     * StringUtil.substring("abc", -2, -1) = "b"
     * StringUtil.substring("abc", -4, 2)  = "ab"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param start ��ʼ�������Ϊ�����ʾ��β������
     * @param end ����������������Ϊ�����ʾ��β������
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length() + end;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return EMPTY_STRING;
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * ȡ�ó���Ϊָ���ַ��������ߵ��Ӵ���
     * <pre>
     * StringUtil.left(null, *)    = null
     * StringUtil.left(*, -ve)     = ""
     * StringUtil.left("", *)      = ""
     * StringUtil.left("abc", 0)   = ""
     * StringUtil.left("abc", 2)   = "ab"
     * StringUtil.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str �ַ�
     * @param len �����Ӵ��ĳ���
     *
     * @return �Ӵ������ԭʼ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return EMPTY_STRING;
        }

        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    /**
     * ȡ�ó���Ϊָ���ַ�������ұߵ��Ӵ���
     * <pre>
     * StringUtil.right(null, *)    = null
     * StringUtil.right(*, -ve)     = ""
     * StringUtil.right("", *)      = ""
     * StringUtil.right("abc", 0)   = ""
     * StringUtil.right("abc", 2)   = "bc"
     * StringUtil.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str �ַ�
     * @param len �����Ӵ��ĳ���
     *
     * @return �Ӵ������ԭʼ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return EMPTY_STRING;
        }

        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(str.length() - len);
        }
    }

    /**
     * ȡ�ô�ָ������ʼ����ġ�����Ϊָ���ַ�����Ӵ���
     * <pre>
     * StringUtil.mid(null, *, *)    = null
     * StringUtil.mid(*, *, -ve)     = ""
     * StringUtil.mid("", 0, *)      = ""
     * StringUtil.mid("abc", 0, 2)   = "ab"
     * StringUtil.mid("abc", 0, 4)   = "abc"
     * StringUtil.mid("abc", 2, 4)   = "c"
     * StringUtil.mid("abc", 4, 2)   = ""
     * StringUtil.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str �ַ�
     * @param pos ��ʼ�������Ϊ��������<code>0</code>
     * @param len �Ӵ��ĳ��ȣ����Ϊ������������Ϊ<code>0</code>
     *
     * @return �Ӵ������ԭʼ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }

        if ((len < 0) || (pos > str.length())) {
            return EMPTY_STRING;
        }

        if (pos < 0) {
            pos = 0;
        }

        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        } else {
            return str.substring(pos, pos + len);
        }
    }

    /* ============================================================================ */
    /*  ������ȡ�Ӵ�����                                                          */
    /* ============================================================================ */

    /**
     * ȡ�õ�һ�����ֵķָ��Ӵ�֮ǰ���Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>��δ�ҵ����Ӵ����򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.substringBefore(null, *)      = null
     * StringUtil.substringBefore("", *)        = ""
     * StringUtil.substringBefore("abc", "a")   = ""
     * StringUtil.substringBefore("abcba", "b") = "a"
     * StringUtil.substringBefore("abc", "c")   = "ab"
     * StringUtil.substringBefore("abc", "d")   = "abc"
     * StringUtil.substringBefore("abc", "")    = ""
     * StringUtil.substringBefore("abc", null)  = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param separator Ҫ�����ķָ��Ӵ�
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substringBefore(String str, String separator) {
        if ((str == null) || (separator == null) || (str.length() == 0)) {
            return str;
        }

        if (separator.length() == 0) {
            return EMPTY_STRING;
        }

        int pos = str.indexOf(separator);

        if (pos == -1) {
            return str;
        }

        return str.substring(0, pos);
    }

    /**
     * ȡ�õ�һ�����ֵķָ��Ӵ�֮����Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>��δ�ҵ����Ӵ����򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.substringAfter(null, *)      = null
     * StringUtil.substringAfter("", *)        = ""
     * StringUtil.substringAfter(*, null)      = ""
     * StringUtil.substringAfter("abc", "a")   = "bc"
     * StringUtil.substringAfter("abcba", "b") = "cba"
     * StringUtil.substringAfter("abc", "c")   = ""
     * StringUtil.substringAfter("abc", "d")   = ""
     * StringUtil.substringAfter("abc", "")    = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param separator Ҫ�����ķָ��Ӵ�
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substringAfter(String str, String separator) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if (separator == null) {
            return EMPTY_STRING;
        }

        int pos = str.indexOf(separator);

        if (pos == -1) {
            return EMPTY_STRING;
        }

        return str.substring(pos + separator.length());
    }

    /**
     * ȡ�����һ���ķָ��Ӵ�֮ǰ���Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>��δ�ҵ����Ӵ����򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.substringBeforeLast(null, *)      = null
     * StringUtil.substringBeforeLast("", *)        = ""
     * StringUtil.substringBeforeLast("abcba", "b") = "abc"
     * StringUtil.substringBeforeLast("abc", "c")   = "ab"
     * StringUtil.substringBeforeLast("a", "a")     = ""
     * StringUtil.substringBeforeLast("a", "z")     = "a"
     * StringUtil.substringBeforeLast("a", null)    = "a"
     * StringUtil.substringBeforeLast("a", "")      = "a"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param separator Ҫ�����ķָ��Ӵ�
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substringBeforeLast(String str, String separator) {
        if ((str == null) || (separator == null) || (str.length() == 0)
                    || (separator.length() == 0)) {
            return str;
        }

        int pos = str.lastIndexOf(separator);

        if (pos == -1) {
            return str;
        }

        return str.substring(0, pos);
    }

    /**
     * ȡ�����һ���ķָ��Ӵ�֮����Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>��δ�ҵ����Ӵ����򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.substringAfterLast(null, *)      = null
     * StringUtil.substringAfterLast("", *)        = ""
     * StringUtil.substringAfterLast(*, "")        = ""
     * StringUtil.substringAfterLast(*, null)      = ""
     * StringUtil.substringAfterLast("abc", "a")   = "bc"
     * StringUtil.substringAfterLast("abcba", "b") = "a"
     * StringUtil.substringAfterLast("abc", "c")   = ""
     * StringUtil.substringAfterLast("a", "a")     = ""
     * StringUtil.substringAfterLast("a", "z")     = ""
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param separator Ҫ�����ķָ��Ӵ�
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String substringAfterLast(String str, String separator) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if ((separator == null) || (separator.length() == 0)) {
            return EMPTY_STRING;
        }

        int pos = str.lastIndexOf(separator);

        if ((pos == -1) || (pos == (str.length() - separator.length()))) {
            return EMPTY_STRING;
        }

        return str.substring(pos + separator.length());
    }

    /**
     * ȡ��ָ���ָ����ǰ���γ���֮����Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * <pre>
     * StringUtil.substringBetween(null, *)            = null
     * StringUtil.substringBetween("", "")             = ""
     * StringUtil.substringBetween("", "tag")          = null
     * StringUtil.substringBetween("tagabctag", null)  = null
     * StringUtil.substringBetween("tagabctag", "")    = ""
     * StringUtil.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param tag Ҫ�����ķָ��Ӵ�
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>��δ�ҵ��ָ��Ӵ����򷵻�<code>null</code>
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag, 0);
    }

    /**
     * ȡ�������ָ���֮����Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * <pre>
     * StringUtil.substringBetween(null, *, *)          = null
     * StringUtil.substringBetween("", "", "")          = ""
     * StringUtil.substringBetween("", "", "tag")       = null
     * StringUtil.substringBetween("", "tag", "tag")    = null
     * StringUtil.substringBetween("yabcz", null, null) = null
     * StringUtil.substringBetween("yabcz", "", "")     = ""
     * StringUtil.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtil.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param open Ҫ�����ķָ��Ӵ�1
     * @param close Ҫ�����ķָ��Ӵ�2
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>��δ�ҵ��ָ��Ӵ����򷵻�<code>null</code>
     */
    public static String substringBetween(String str, String open, String close) {
        return substringBetween(str, open, close, 0);
    }

    /**
     * ȡ�������ָ���֮����Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ���ָ��Ӵ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * <pre>
     * StringUtil.substringBetween(null, *, *)          = null
     * StringUtil.substringBetween("", "", "")          = ""
     * StringUtil.substringBetween("", "", "tag")       = null
     * StringUtil.substringBetween("", "tag", "tag")    = null
     * StringUtil.substringBetween("yabcz", null, null) = null
     * StringUtil.substringBetween("yabcz", "", "")     = ""
     * StringUtil.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtil.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     * </p>
     *
     * @param str �ַ�
     * @param open Ҫ�����ķָ��Ӵ�1
     * @param close Ҫ�����ķָ��Ӵ�2
     * @param fromIndex ��ָ��index������
     *
     * @return �Ӵ������ԭʼ��Ϊ<code>null</code>��δ�ҵ��ָ��Ӵ����򷵻�<code>null</code>
     */
    public static String substringBetween(String str, String open, String close, int fromIndex) {
        if ((str == null) || (open == null) || (close == null)) {
            return null;
        }

        int start = str.indexOf(open, fromIndex);

        if (start != -1) {
            int end = str.indexOf(close, start + open.length());

            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }

        return null;
    }

    /* ============================================================================ */
    /*  ɾ���ַ�                                                                  */
    /* ============================================================================ */

    /**
     * ɾ��������<code>Character.isWhitespace(char)</code>������Ŀհס�
     * <pre>
     * StringUtil.deleteWhitespace(null)         = null
     * StringUtil.deleteWhitespace("")           = ""
     * StringUtil.deleteWhitespace("abc")        = "abc"
     * StringUtil.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str Ҫ������ַ�
     *
     * @return ȥ�հ׺���ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String deleteWhitespace(String str) {
        if (str == null) {
            return null;
        }

        int          sz     = str.length();
        StringBuffer buffer = new StringBuffer(sz);

        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }

        return buffer.toString();
    }

    /* ============================================================================ */
    /*  �滻�Ӵ���                                                                  */
    /* ============================================================================ */

    /**
     * �滻ָ�����Ӵ���ֻ�滻��һ�����ֵ��Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>�򷵻�<code>null</code>�����ָ���Ӵ�Ϊ<code>null</code>���򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.replaceOnce(null, *, *)        = null
     * StringUtil.replaceOnce("", *, *)          = ""
     * StringUtil.replaceOnce("aba", null, null) = "aba"
     * StringUtil.replaceOnce("aba", null, null) = "aba"
     * StringUtil.replaceOnce("aba", "a", null)  = "aba"
     * StringUtil.replaceOnce("aba", "a", "")    = "ba"
     * StringUtil.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     * </p>
     *
     * @param text Ҫɨ����ַ�
     * @param repl Ҫ�������Ӵ�
     * @param with �滻�ַ�
     *
     * @return ���滻����ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * �滻ָ�����Ӵ����滻���г��ֵ��Ӵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>�򷵻�<code>null</code>�����ָ���Ӵ�Ϊ<code>null</code>���򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.replace(null, *, *)        = null
     * StringUtil.replace("", *, *)          = ""
     * StringUtil.replace("aba", null, null) = "aba"
     * StringUtil.replace("aba", null, null) = "aba"
     * StringUtil.replace("aba", "a", null)  = "aba"
     * StringUtil.replace("aba", "a", "")    = "b"
     * StringUtil.replace("aba", "a", "z")   = "zbz"
     * </pre>
     * </p>
     *
     * @param text Ҫɨ����ַ�
     * @param repl Ҫ�������Ӵ�
     * @param with �滻�ַ�
     *
     * @return ���滻����ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * �滻ָ�����Ӵ����滻ָ���Ĵ���
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>�򷵻�<code>null</code>�����ָ���Ӵ�Ϊ<code>null</code>���򷵻�ԭ�ַ�
     * <pre>
     * StringUtil.replace(null, *, *, *)         = null
     * StringUtil.replace("", *, *, *)           = ""
     * StringUtil.replace("abaa", null, null, 1) = "abaa"
     * StringUtil.replace("abaa", null, null, 1) = "abaa"
     * StringUtil.replace("abaa", "a", null, 1)  = "abaa"
     * StringUtil.replace("abaa", "a", "", 1)    = "baa"
     * StringUtil.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtil.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtil.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtil.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     * </p>
     *
     * @param text Ҫɨ����ַ�
     * @param repl Ҫ�������Ӵ�
     * @param with �滻�ַ�
     * @param max maximum number of values to replace, or <code>-1</code> if no maximum
     *
     * @return ���滻����ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.length() == 0)
                    || (max == 0)) {
            return text;
        }

        StringBuffer buf   = new StringBuffer(text.length());
        int          start = 0;
        int          end   = 0;

        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }

        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * ���ַ�������ָ�����ַ��滻����һ����
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>�򷵻�<code>null</code>��
     * <pre>
     * StringUtil.replaceChars(null, *, *)        = null
     * StringUtil.replaceChars("", *, *)          = ""
     * StringUtil.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtil.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     * </p>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChar Ҫ�������ַ�
     * @param replaceChar �滻�ַ�
     *
     * @return ���滻����ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }

        return str.replace(searchChar, replaceChar);
    }

    /**
     * ���ַ�������ָ�����ַ��滻����һ����
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>�򷵻�<code>null</code>����������ַ�Ϊ<code>null</code>��գ��򷵻�ԭ�ַ�
     * </p>
     * 
     * <p>
     * ���磺 <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>��
     * </p>
     * 
     * <p>
     * ͨ�������ַ���滻�ַ��ǵȳ��ģ���������ַ���滻�ַ����������ַ�ɾ�� ��������ַ���滻�ַ�̣���ȱ�ٵ��ַ����ԡ�
     * <pre>
     * StringUtil.replaceChars(null, *, *)           = null
     * StringUtil.replaceChars("", *, *)             = ""
     * StringUtil.replaceChars("abc", null, *)       = "abc"
     * StringUtil.replaceChars("abc", "", *)         = "abc"
     * StringUtil.replaceChars("abc", "b", null)     = "ac"
     * StringUtil.replaceChars("abc", "b", "")       = "ac"
     * StringUtil.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtil.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtil.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     * </p>
     *
     * @param str Ҫɨ����ַ�
     * @param searchChars Ҫ�������ַ�
     * @param replaceChars �滻�ַ�
     *
     * @return ���滻����ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                    || (searchChars.length() == 0)) {
            return str;
        }

        char[]  chars    = str.toCharArray();
        int     len      = chars.length;
        boolean modified = false;

        for (int i = 0, isize = searchChars.length(); i < isize; i++) {
            char searchChar = searchChars.charAt(i);

            if ((replaceChars == null) || (i >= replaceChars.length())) {
                // ɾ��
                int pos = 0;

                for (int j = 0; j < len; j++) {
                    if (chars[j] != searchChar) {
                        chars[pos++] = chars[j];
                    } else {
                        modified = true;
                    }
                }

                len = pos;
            } else {
                // �滻
                for (int j = 0; j < len; j++) {
                    if (chars[j] == searchChar) {
                        chars[j]     = replaceChars.charAt(i);
                        modified     = true;
                    }
                }
            }
        }

        if (!modified) {
            return str;
        }

        return new String(chars, 0, len);
    }

    /**
     * ��ָ�����Ӵ�����һָ���Ӵ����ǡ�
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>�� ��������ֵ��������<code>0</code>��Խ�������ֵ�������ó��ַ�ĳ�����ͬ��ֵ��
     * <pre>
     * StringUtil.overlay(null, *, *, *)            = null
     * StringUtil.overlay("", "abc", 0, 0)          = "abc"
     * StringUtil.overlay("abcdef", null, 2, 4)     = "abef"
     * StringUtil.overlay("abcdef", "", 2, 4)       = "abef"
     * StringUtil.overlay("abcdef", "", 4, 2)       = "abef"
     * StringUtil.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringUtil.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringUtil.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringUtil.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringUtil.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringUtil.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     * </p>
     *
     * @param str Ҫɨ����ַ�
     * @param overlay �������ǵ��ַ�
     * @param start ��ʼ����
     * @param end ��������
     *
     * @return �����Ǻ���ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }

        if (overlay == null) {
            overlay = EMPTY_STRING;
        }

        int len = str.length();

        if (start < 0) {
            start = 0;
        }

        if (start > len) {
            start = len;
        }

        if (end < 0) {
            end = 0;
        }

        if (end > len) {
            end = len;
        }

        if (start > end) {
            int temp = start;

            start     = end;
            end       = temp;
        }

        return new StringBuffer((len + start) - end + overlay.length() + 1).append(str.substring(
                0, start)).append(overlay).append(str.substring(end)).toString();
    }

    /* ============================================================================ */
    /*  Perl����chomp��chop����                                                 */
    /* ============================================================================ */

    /**
     * ɾ���ַ�ĩβ�Ļ��з�����ַ��Ի��н�β����ʲôҲ������
     * 
     * <p>
     * ���з����������Σ�&quot;<code>\n</code>&quot;��&quot;<code>\r</code>&quot;��&quot;<code>\r\n</code>&quot;��
     * <pre>
     * StringUtil.chomp(null)          = null
     * StringUtil.chomp("")            = ""
     * StringUtil.chomp("abc \r")      = "abc "
     * StringUtil.chomp("abc\n")       = "abc"
     * StringUtil.chomp("abc\r\n")     = "abc"
     * StringUtil.chomp("abc\r\n\r\n") = "abc\r\n"
     * StringUtil.chomp("abc\n\r")     = "abc\n"
     * StringUtil.chomp("abc\n\rabc")  = "abc\n\rabc"
     * StringUtil.chomp("\r")          = ""
     * StringUtil.chomp("\n")          = ""
     * StringUtil.chomp("\r\n")        = ""
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ���Ի��н�β���ַ����ԭʼ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String chomp(String str) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);

            if ((ch == '\r') || (ch == '\n')) {
                return EMPTY_STRING;
            } else {
                return str;
            }
        }

        int  lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {
        } else {
            lastIdx++;
        }

        return str.substring(0, lastIdx);
    }

    /**
     * ɾ���ַ�ĩβ��ָ���ַ�����ַ��Ը��ַ��β����ʲôҲ������
     * <pre>
     * StringUtil.chomp(null, *)         = null
     * StringUtil.chomp("", *)           = ""
     * StringUtil.chomp("foobar", "bar") = "foo"
     * StringUtil.chomp("foobar", "baz") = "foobar"
     * StringUtil.chomp("foo", "foo")    = ""
     * StringUtil.chomp("foo ", "foo")   = "foo "
     * StringUtil.chomp(" foo", "foo")   = " "
     * StringUtil.chomp("foo", "foooo")  = "foo"
     * StringUtil.chomp("foo", "")       = "foo"
     * StringUtil.chomp("foo", null)     = "foo"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param separator Ҫɾ����ַ�
     *
     * @return ����ָ���ַ��β���ַ����ԭʼ�ִ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String chomp(String str, String separator) {
        if ((str == null) || (str.length() == 0) || (separator == null)) {
            return str;
        }

        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }

        return str;
    }

    /**
     * ɾ�����һ���ַ�
     * 
     * <p>
     * ����ַ���<code>\r\n</code>��β����ͬʱɾ�����ǡ�
     * <pre>
     * StringUtil.chop(null)          = null
     * StringUtil.chop("")            = ""
     * StringUtil.chop("abc \r")      = "abc "
     * StringUtil.chop("abc\n")       = "abc"
     * StringUtil.chop("abc\r\n")     = "abc"
     * StringUtil.chop("abc")         = "ab"
     * StringUtil.chop("abc\nabc")    = "abc\nab"
     * StringUtil.chop("a")           = ""
     * StringUtil.chop("\r")          = ""
     * StringUtil.chop("\n")          = ""
     * StringUtil.chop("\r\n")        = ""
     * </pre>
     * </p>
     *
     * @param str Ҫ������ַ�
     *
     * @return ɾ�����һ���ַ���ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String chop(String str) {
        if (str == null) {
            return null;
        }

        int strLen = str.length();

        if (strLen < 2) {
            return EMPTY_STRING;
        }

        int    lastIdx = strLen - 1;
        String ret  = str.substring(0, lastIdx);
        char   last = str.charAt(lastIdx);

        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }

        return ret;
    }

    /* ============================================================================ */
    /*  �ظ�/�����ַ�                                                           */
    /* ============================================================================ */

    /**
     * ��ָ���ַ��ظ�n�顣
     * <pre>
     * StringUtil.repeat(null, 2)   = null
     * StringUtil.repeat("", 0)     = ""
     * StringUtil.repeat("", 2)     = ""
     * StringUtil.repeat("a", 3)    = "aaa"
     * StringUtil.repeat("ab", 2)   = "abab"
     * StringUtil.repeat("abcd", 2) = "abcdabcd"
     * StringUtil.repeat("a", -2)   = ""
     * </pre>
     *
     * @param str Ҫ�ظ����ַ�
     * @param repeat �ظ��������С��<code>0</code>������<code>0</code>
     *
     * @return �ظ�n�ε��ַ����ԭʼ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }

        if (repeat <= 0) {
            return EMPTY_STRING;
        }

        int inputLength = str.length();

        if ((repeat == 1) || (inputLength == 0)) {
            return str;
        }

        int outputLength = inputLength * repeat;

        switch (inputLength) {
            case 1:

                char   ch      = str.charAt(0);
                char[] output1 = new char[outputLength];

                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }

                return new String(output1);

            case 2:

                char   ch0     = str.charAt(0);
                char   ch1     = str.charAt(1);
                char[] output2 = new char[outputLength];

                for (int i = (repeat * 2) - 2; i >= 0; i--, i--) {
                    output2[i]         = ch0;
                    output2[i + 1]     = ch1;
                }

                return new String(output2);

            default:

                StringBuffer buf = new StringBuffer(outputLength);

                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }

                return buf.toString();
        }
    }

    /**
     * ��չ��������ַ��ÿո�<code>' '</code>����ұߡ�
     * <pre>
     * StringUtil.alignLeft(null, *)   = null
     * StringUtil.alignLeft("", 3)     = "   "
     * StringUtil.alignLeft("bat", 3)  = "bat"
     * StringUtil.alignLeft("bat", 5)  = "bat  "
     * StringUtil.alignLeft("bat", 1)  = "bat"
     * StringUtil.alignLeft("bat", -1) = "bat"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignLeft(String str, int size) {
        return alignLeft(str, size, ' ');
    }

    /**
     * ��չ��������ַ���ָ���ַ�����ұߡ�
     * <pre>
     * StringUtil.alignLeft(null, *, *)     = null
     * StringUtil.alignLeft("", 3, 'z')     = "zzz"
     * StringUtil.alignLeft("bat", 3, 'z')  = "bat"
     * StringUtil.alignLeft("bat", 5, 'z')  = "batzz"
     * StringUtil.alignLeft("bat", 1, 'z')  = "bat"
     * StringUtil.alignLeft("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padChar ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignLeft(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length();

        if (pads <= 0) {
            return str;
        }

        return alignLeft(str, size, String.valueOf(padChar));
    }

    /**
     * ��չ��������ַ���ָ���ַ�����ұߡ�
     * <pre>
     * StringUtil.alignLeft(null, *, *)      = null
     * StringUtil.alignLeft("", 3, "z")      = "zzz"
     * StringUtil.alignLeft("bat", 3, "yz")  = "bat"
     * StringUtil.alignLeft("bat", 5, "yz")  = "batyz"
     * StringUtil.alignLeft("bat", 8, "yz")  = "batyzyzy"
     * StringUtil.alignLeft("bat", 1, "yz")  = "bat"
     * StringUtil.alignLeft("bat", -1, "yz") = "bat"
     * StringUtil.alignLeft("bat", 5, null)  = "bat  "
     * StringUtil.alignLeft("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padStr ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignLeft(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads   = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding  = new char[pads];
            char[] padChars = padStr.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return str.concat(new String(padding));
        }
    }

    /**
     * ��չ���Ҷ����ַ��ÿո�<code>' '</code>�����ߡ�
     * <pre>
     * StringUtil.alignRight(null, *)   = null
     * StringUtil.alignRight("", 3)     = "   "
     * StringUtil.alignRight("bat", 3)  = "bat"
     * StringUtil.alignRight("bat", 5)  = "  bat"
     * StringUtil.alignRight("bat", 1)  = "bat"
     * StringUtil.alignRight("bat", -1) = "bat"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignRight(String str, int size) {
        return alignRight(str, size, ' ');
    }

    /**
     * ��չ���Ҷ����ַ���ָ���ַ������ߡ�
     * <pre>
     * StringUtil.alignRight(null, *, *)     = null
     * StringUtil.alignRight("", 3, 'z')     = "zzz"
     * StringUtil.alignRight("bat", 3, 'z')  = "bat"
     * StringUtil.alignRight("bat", 5, 'z')  = "zzbat"
     * StringUtil.alignRight("bat", 1, 'z')  = "bat"
     * StringUtil.alignRight("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padChar ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignRight(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length();

        if (pads <= 0) {
            return str;
        }

        return alignRight(str, size, String.valueOf(padChar));
    }

    /**
     * ��չ���Ҷ����ַ���ָ���ַ������ߡ�
     * <pre>
     * StringUtil.alignRight(null, *, *)      = null
     * StringUtil.alignRight("", 3, "z")      = "zzz"
     * StringUtil.alignRight("bat", 3, "yz")  = "bat"
     * StringUtil.alignRight("bat", 5, "yz")  = "yzbat"
     * StringUtil.alignRight("bat", 8, "yz")  = "yzyzybat"
     * StringUtil.alignRight("bat", 1, "yz")  = "bat"
     * StringUtil.alignRight("bat", -1, "yz") = "bat"
     * StringUtil.alignRight("bat", 5, null)  = "  bat"
     * StringUtil.alignRight("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padStr ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String alignRight(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads   = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding  = new char[pads];
            char[] padChars = padStr.toCharArray();

            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }

            return new String(padding).concat(str);
        }
    }

    /**
     * ��չ�������ַ��ÿո�<code>' '</code>������ߡ�
     * <pre>
     * StringUtil.center(null, *)   = null
     * StringUtil.center("", 4)     = "    "
     * StringUtil.center("ab", -1)  = "ab"
     * StringUtil.center("ab", 4)   = " ab "
     * StringUtil.center("abcd", 2) = "abcd"
     * StringUtil.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    /**
     * ��չ�������ַ���ָ���ַ�������ߡ�
     * <pre>
     * StringUtil.center(null, *, *)     = null
     * StringUtil.center("", 4, ' ')     = "    "
     * StringUtil.center("ab", -1, ' ')  = "ab"
     * StringUtil.center("ab", 4, ' ')   = " ab "
     * StringUtil.center("abcd", 2, ' ') = "abcd"
     * StringUtil.center("a", 4, ' ')    = " a  "
     * StringUtil.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padChar ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String center(String str, int size, char padChar) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str     = alignRight(str, strLen + (pads / 2), padChar);
        str     = alignLeft(str, size, padChar);
        return str;
    }

    /**
     * ��չ�������ַ���ָ���ַ�������ߡ�
     * <pre>
     * StringUtil.center(null, *, *)     = null
     * StringUtil.center("", 4, " ")     = "    "
     * StringUtil.center("ab", -1, " ")  = "ab"
     * StringUtil.center("ab", 4, " ")   = " ab "
     * StringUtil.center("abcd", 2, " ") = "abcd"
     * StringUtil.center("a", 4, " ")    = " a  "
     * StringUtil.center("a", 4, "yz")   = "yayz"
     * StringUtil.center("abc", 7, null) = "  abc  "
     * StringUtil.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str Ҫ������ַ�
     * @param size ��չ�ַ�ָ�����
     * @param padStr ����ַ�
     *
     * @return ��չ����ַ�����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String center(String str, int size, String padStr) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str     = alignRight(str, strLen + (pads / 2), padStr);
        str     = alignLeft(str, size, padStr);
        return str;
    }

    /* ============================================================================ */
    /*  ��ת�ַ�                                                                */
    /* ============================================================================ */

    /**
     * ��ת�ַ��е��ַ�˳��
     * 
     * <p>
     * ����ַ�Ϊ<code>null</code>���򷵻�<code>null</code>��
     * </p>
     * <pre>
     * StringUtil.reverse(null)  = null
     * StringUtil.reverse("")    = ""
     * StringUtil.reverse("bat") = "tab"
     * </pre>
     *
     * @param str Ҫ��ת���ַ�
     *
     * @return ��ת����ַ����ԭ�ַ�Ϊ<code>null</code>���򷵻�<code>null</code>
     */
    public static String reverse(String str) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        return new StringBuffer(str).reverse().toString();
    }

    /* ============================================================================ */
    /*  ȡ���ַ�����ԡ�                                                          */
    /* ============================================================================ */

    /**
     * ���ַ�ת����ָ�����ȵ����ԣ����磺 ��"Now is the time for all good men"ת����"Now is the time for..."��
     * 
     * <ul>
     * <li>
     * ���<code>str</code>��<code>maxWidth</code>�̣�ֱ�ӷ��أ�
     * </li>
     * <li>
     * ������ת�������ԣ�<code>substring(str, 0, max-3) + "..."</code>��
     * </li>
     * <li>
     * ���<code>maxWidth</code>С��<code>4</code>�׳�<code>IllegalArgumentException</code>��
     * </li>
     * <li>
     * ���ص��ַ����ܳ���ָ����<code>maxWidth</code>��
     * </li>
     * </ul>
     * 
     * <pre>
     * StringUtil.abbreviate(null, *)      = null
     * StringUtil.abbreviate("", 4)        = ""
     * StringUtil.abbreviate("abcdefg", 6) = "abc..."
     * StringUtil.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtil.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtil.abbreviate("abcdefg", 4) = "a..."
     * StringUtil.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str Ҫ�����ַ�
     * @param maxWidth ��󳤶ȣ���С��<code>4</code>�����С��<code>4</code>������<code>4</code>
     *
     * @return �ַ����ԣ����ԭʼ�ַ�Ϊ<code>null</code>�򷵻�<code>null</code>
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * ���ַ�ת����ָ�����ȵ����ԣ����磺 ��"Now is the time for all good men"ת����"...is the time for..."��
     * 
     * <p>
     * ��<code>abbreviate(String, int)</code>���ƣ�����������һ������߽硱ƫ������
     * ע�⣬����߽硱�����ַ�δ�س����ڽ���ַ������ߣ���һ�������ڽ���ַ��С�
     * </p>
     * 
     * <p>
     * ���ص��ַ����ܳ���ָ����<code>maxWidth</code>��
     * <pre>
     * StringUtil.abbreviate(null, *, *)                = null
     * StringUtil.abbreviate("", 0, 4)                  = ""
     * StringUtil.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringUtil.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringUtil.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringUtil.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringUtil.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringUtil.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringUtil.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringUtil.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringUtil.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringUtil.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringUtil.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     * </p>
     *
     * @param str Ҫ�����ַ�
     * @param offset ��߽�ƫ����
     * @param maxWidth ��󳤶ȣ���С��<code>4</code>�����С��<code>4</code>������<code>4</code>
     *
     * @return �ַ����ԣ����ԭʼ�ַ�Ϊ<code>null</code>�򷵻�<code>null</code>
     */
    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }

        // ���������
        if (maxWidth < 4) {
            maxWidth = 4;
        }

        if (str.length() <= maxWidth) {
            return str;
        }

        if (offset > str.length()) {
            offset = str.length();
        }

        if ((str.length() - offset) < (maxWidth - 3)) {
            offset = str.length() - (maxWidth - 3);
        }

        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }

        // ���������
        if (maxWidth < 7) {
            maxWidth = 7;
        }

        if ((offset + (maxWidth - 3)) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }

        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    /* ============================================================================ */
    /*  �Ƚ������ַ����ͬ��                                                      */
    /*                                                                              */
    /*  �����ַ�֮��Ĳ��죬�Ƚ��ַ�����ƶȡ�                                  */
    /* ============================================================================ */

    /**
     * �Ƚ������ַ�ȡ�õڶ����ַ��У��͵�һ���ַ�ͬ�Ĳ��֡�
     * <pre>
     * StringUtil.difference("i am a machine", "i am a robot")  = "robot"
     * StringUtil.difference(null, null)                        = null
     * StringUtil.difference("", "")                            = ""
     * StringUtil.difference("", null)                          = ""
     * StringUtil.difference("", "abc")                         = "abc"
     * StringUtil.difference("abc", "")                         = ""
     * StringUtil.difference("abc", "abc")                      = ""
     * StringUtil.difference("ab", "abxyz")                     = "xyz"
     * StringUtil.difference("abcde", "abxyz")                  = "xyz"
     * StringUtil.difference("abcde", "xyz")                    = "xyz"
     * </pre>
     *
     * @param str1 �ַ�1
     * @param str2 �ַ�2
     *
     * @return �ڶ����ַ��У��͵�һ���ַ�ͬ�Ĳ��֡���������ַ���ͬ���򷵻ؿ��ַ�<code>""</code>
     */
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }

        if (str2 == null) {
            return str1;
        }

        int index = indexOfDifference(str1, str2);

        if (index == -1) {
            return EMPTY_STRING;
        }

        return str2.substring(index);
    }

    /**
     * �Ƚ������ַ�ȡ�����ַ�ʼ��ͬ������ֵ��
     * <pre>
     * StringUtil.indexOfDifference("i am a machine", "i am a robot")   = 7
     * StringUtil.indexOfDifference(null, null)                         = -1
     * StringUtil.indexOfDifference("", null)                           = -1
     * StringUtil.indexOfDifference("", "")                             = -1
     * StringUtil.indexOfDifference("", "abc")                          = 0
     * StringUtil.indexOfDifference("abc", "")                          = 0
     * StringUtil.indexOfDifference("abc", "abc")                       = -1
     * StringUtil.indexOfDifference("ab", "abxyz")                      = 2
     * StringUtil.indexOfDifference("abcde", "abxyz")                   = 2
     * StringUtil.indexOfDifference("abcde", "xyz")                     = 0
     * </pre>
     *
     * @param str1 �ַ�1
     * @param str2 �ַ�2
     *
     * @return ���ַ�ʼ������������ֵ��������ַ���ͬ���򷵻�<code>-1</code>
     */
    public static int indexOfDifference(String str1, String str2) {
        if ((str1 == str2) || (str1 == null) || (str2 == null)) {
            return -1;
        }

        int i;

        for (i = 0; (i < str1.length()) && (i < str2.length()); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }

        if ((i < str2.length()) || (i < str1.length())) {
            return i;
        }

        return -1;
    }

    /**
     * ȡ�������ַ�����ƶȣ�<code>0</code>����ַ���ȣ�����Խ���ʾ�ַ�Խ����
     * 
     * <p>
     * ����㷨ȡ��<a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>��
     * �������Ǵ��ַ�1ת�䵽�ַ�2����Ҫ��ɾ�������滻�Ĳ�����
     * </p>
     * <pre>
     * StringUtil.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtil.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtil.getLevenshteinDistance("","")               = 0
     * StringUtil.getLevenshteinDistance("","a")              = 1
     * StringUtil.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtil.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtil.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtil.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtil.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtil.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtil.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s ��һ���ַ������<code>null</code>���������ַ�
     * @param t �ڶ����ַ������<code>null</code>���������ַ�
     *
     * @return ���ƶ�ֵ
     */
    public static int getLevenshteinDistance(String s, String t) {
        s     = defaultIfNull(s);
        t     = defaultIfNull(t);

        int[][] d; // matrix
        int     n; // length of s
        int     m; // length of t
        int     i; // iterates through s
        int     j; // iterates through t
        char    s_i; // ith character of s
        char    t_j; // jth character of t
        int     cost; // cost

        // Step 1
        n     = s.length();
        m     = t.length();

        if (n == 0) {
            return m;
        }

        if (m == 0) {
            return n;
        }

        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }

    /**
     * ȡ����С��
     *
     * @param a ����1
     * @param b ����2
     * @param c ����3
     *
     * @return ������е���Сֵ
     */
    private static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }

        if (c < a) {
            a = c;
        }

        return a;
    }
}
