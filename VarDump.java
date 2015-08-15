package varDump;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class VarDump {

	// eg. prints 10 elements of array
	public static Integer maxGetIndex = 10;

	public static HashMap<String, ArrayList<Object>> additionalMethods;

	public static void addStandardAdditionalMethods() {
		if (additionalMethods == null) {
			additionalMethods = new HashMap<String, ArrayList<Object>>();
		}
		if (additionalMethods.containsKey("get") == false
				|| (additionalMethods.get("get") != null && additionalMethods
						.get("get").size() != maxGetIndex)) {
			ArrayList<Object> args;

			//arguments for get() method
			args = new ArrayList<Object>();
			for (Integer i = 0; i < maxGetIndex; i++) {
				args.add(i);
			}

			additionalMethods.put("get", args);
		}

	}

	public static String varDump(Object o) {
		return varDump(o, 2);
	}

	public static String varDump(Object o, int maxLvl) {
		return varDump(o, maxLvl, true);
	}

	public static String varDump(Object o, int maxLvl,
			boolean addStandartMethods) {
		if (addStandartMethods == true) {
			addStandardAdditionalMethods();
		}
		return varDump(o, maxLvl, 0);
	}

	// Do not use this method directly
	public static String varDump(Object o, int maxLvl, int currentLvl) {
		String prefix = "";
		String prefixContent = "    ";
		for (int i = 0; i < currentLvl; i++) {
			prefix += prefixContent;
		}
		if (currentLvl == maxLvl)
			return "";
		if (o == null || o.getClass() == null
				|| o.getClass().getSimpleName() == null)
			return "";
		String s = prefix + o.getClass().getSimpleName() + ": [\n";

		Field[] fields = o.getClass().getDeclaredFields();
		Method[] methods = o.getClass().getMethods();

		for (int i = 0; i < fields.length; i++) {

			try {
				String fieldName = fields[i].getName();

				if (Modifier.isPublic(fields[i].getModifiers())) {
					s += prefix + prefixContent + "(" + fieldName + " = "
							+ fields[i].get(o) + ") \n";
					String tmpS = varDump(fields[i].get(o), maxLvl,
							currentLvl + 1);

					s += tmpS;
					continue;
				}

			} catch (IllegalArgumentException e) {

			} catch (IllegalAccessException e) {

			}

		}

		for (Method m : methods) {
			String mName = m.getName();

			if (mName.startsWith("get")
					|| additionalMethods.keySet().contains(mName)) {

				if (Modifier.isPublic(m.getModifiers())) {
					if (additionalMethods.keySet().contains(mName)
							&& additionalMethods.get(mName).size() != 0) {

						for (Object oArg : additionalMethods.get(mName)) {

							try {

								Object oInv = m.invoke(o, oArg);

								s += prefix + prefixContent + "(" + m.getName()
										+ "(" + oArg + ")" + " = " + oInv
										+ ") \n";
								String tmpS = varDump(oInv, maxLvl,
										currentLvl + 1);

								s += tmpS;
							} catch (Exception e) {

							}

						}
					} else {

						try {
							Object oInv = m.invoke(o);
							s += prefix + prefixContent + "(" + m.getName()
									+ "()" + " = " + oInv + ") \n";
							String tmpS = varDump(oInv, maxLvl, currentLvl + 1);

							s += tmpS;
						} catch (Exception e) {

						}

					}
				}
			}
		}
		s += prefix + "]\n";

		return s;

	}

	public static void main(String[] args) {
		ArrayList<String> lst = new ArrayList<String>();
		for (int i = 0; i < 20; i++) {
			lst.add("str" + i);
		}
		System.out.println(varDump(lst, 1));
		System.out.println("\n\n\n\n\n");
		System.out.println(varDump(lst, 2));

		System.out.println("\n\n\n\n\n");
		System.out.println(varDump(new VarDump(), 1));
		
		System.out.println("\n\n\n\n\n");
		System.out.println(varDump(new VarDump(), 2));
	}
}
