package de.persosim.simulator.jaxb;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class provides a singleton {@link JAXBContext} that is initialized to be
 * used with all de.persosim classes known to the current classloader.
 * <p/>
 * Note: as the provided JAXBContext is not Thread-safe neither this singleton
 * implementation is nor needs to be.
 * 
 * @author amay
 * 
 */
public class PersoSimJaxbContextProvider {
	// create/cache JAXBContext

	private static JAXBContext context;

	public static JAXBContext getContext() throws JAXBException {
		if (context == null) {
			context = JAXBContext.newInstance(getJaxbClasses().toArray(
					new Class<?>[] {}));
		}
		return context;
	}

	/**
	 * Returns a collection containing all classes that are JAXB annotated.
	 * 
	 * @return a collection containing all classes that are JAXB annotated
	 */
	public static Collection<Class<?>> getJaxbClasses() {
		try {
			Collection<Class<?>> allClasses = getAllPersoSimClassFromClassLoader();
			return getJaxbAnnotatedClasses(allClasses);
		} catch (IOException e) {
			// can't identify loadable classes, return empty list;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns a Collection of all PersoSim classes.
	 * <p/>
	 * The implementation extracts loadable classes from the class loaders class
	 * path and tries to return according Class objects.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Collection<Class<?>> getAllPersoSimClassFromClassLoader()
			throws IOException {
		ArrayList<Class<?>> persoSimClasses = new ArrayList<>();

		//search for all classes in de.persosim
		String packageName = "de.persosim";
		
		//get class folders from ClassLoader
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		Enumeration<URL> res = classLoader.getResources(packageName.replace('.', '/'));

		while (res.hasMoreElements()) {
			URL curRes = res.nextElement();
			if (curRes.getProtocol().equals("jar")) {
				//extract classes from JAR-file
				String fileName = curRes.getPath();
				fileName = fileName.substring(5); // remove leading "file:"
				fileName = fileName.replaceFirst("!.*", ""); // remove trailing resource path
				persoSimClasses.addAll(getPackageClassesFromJar(new File(fileName), packageName));
			} else {
				//extract classes from class folders
				persoSimClasses.addAll(getAllClassesFromPackage(new File(curRes.getFile()), packageName));
			}
		}

		

		return persoSimClasses;
	}

	/**
	 * Recursively checks all files in the given directory. If potential class
	 * files are found the according class is tried to be loaded by the
	 * ClassLoader. If this is successful the according Class object is added to
	 * the returned Collection.
	 * 
	 * @param packageDir
	 *            directory that contains implementation files for the current
	 *            package
	 * @param packageName
	 *            name of the current package, this is used to build the FQN for
	 *            found classes in order to retrieve {@link Class} objects from
	 *            the {@link ClassLoader}
	 * @return a Collection of {@link Class} objects for all *.class files found
	 *         in the packageDir that can be loaded through the current
	 *         {@link ClassLoader}
	 */
	private static Collection<Class<?>> getAllClassesFromPackage(
			File packageDir, String packageName) {
		if (packageDir == null)
			return Collections.emptySet();
		if (!packageDir.isDirectory()) {
			if (packageDir.getName().endsWith(".jar")) {
				return getPackageClassesFromJar(packageDir, packageName);
			} else {
				return Collections.emptySet();
			}
		}

		ArrayList<Class<?>> classes = new ArrayList<>();
		for (File curFile : packageDir.listFiles()) {
			if (curFile.isDirectory()) {
				classes.addAll(getAllClassesFromPackage(curFile, packageName+"."+curFile.getName()));
			} else {
				String fileName = curFile.getName();
				if (fileName.endsWith(".class")) {
					String className = fileName.substring(0,
							fileName.length() - 6);
					className = packageName + "." + className;
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						// if Class can not be found silently ignore this guess
					}
				}
			}
		}
		return classes;
	}

	/**
	 * Extract all classes defined in the given JarFile that belong to the given
	 * package.
	 * 
	 * @param packageJar
	 *            File referencing the JarFile to be read
	 * @param packageName
	 *            required prefix for classes to be returned
	 * @return Collection of {@link Class} objects for all *.class files found
	 *         in the Jar file that belong to the given package and can be
	 *         loaded through the current {@link ClassLoader}
	 */
	private static Collection<Class<?>> getPackageClassesFromJar(
			File packageJar, String packageName) {
		if ((packageJar == null) || packageJar.isDirectory())
			return Collections.emptySet();
		
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(packageJar);
			
			ArrayList<Class<?>> classes = new ArrayList<>();
			
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry curEntry = jarEntries.nextElement();
				
				String className = curEntry.getName();
				
				if (!className.endsWith(".class")) continue;
				className = className.substring(0,
						className.length() - 6); // remove trailing .class
				
				className = className.replaceAll("/", "."); //replace separators
				
				if (!className.startsWith(packageName)) continue;
				
				try {
					classes.add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					// if Class can not be found silently ignore this guess
				}	
			}
			
			return classes;
				
		} catch (IOException e) {
			// ignore, an empty set is returned at the bottom of this method
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					//ignore;
				}
			}
		}
		
		return Collections.emptySet();
	}

	/**
	 * Filters the provided Collection of classes and returns a new Collection
	 * containing only those classes that are usable by JAXB (according to its
	 * Annotations).
	 * 
	 * @param allClasses
	 *            Collection of classes to be searched
	 * @return Collection containing the subset of classes from allClasses that
	 *         contain JAXB Annotations
	 */
	private static Collection<Class<?>> getJaxbAnnotatedClasses(
			Collection<Class<?>> allClasses) {
		ArrayList<Class<?>> annotatedClasses = new ArrayList<>();
		for (Class<?> curClass : allClasses) {
			for (Annotation curAnnotation : curClass.getAnnotations()) {
				if (curAnnotation.annotationType() == XmlRootElement.class) {
					annotatedClasses.add(curClass);
					break;
				}
			}
		}
		return annotatedClasses;
	}
}