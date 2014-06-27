package com.laptopfreek0.taskkill.objects;

import java.util.Comparator;

public class AppComparer implements Comparator<App>{

	public int compare(App lhs, App rhs) {
		return lhs.getName().compareToIgnoreCase(rhs.getName());
	}
}
