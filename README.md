SysMonFx (working title)
========================
Linas Martusevicius, inlusion Netforms 2014
PROJECT DEPENDS ON THE SIGAR LIBRARY
============================================

The project inherits the Apache Liscence V2.0 from the Sigar library. Project is in a slightly tweaked MVC structure, with all of the classes in their respective folders. Uses the Sigar library for retrieving system information such as CPU load, RAM utilization and Network In/Out load. Sigar library .jar and additional library files must be in the lib/ folder, relative to the main .jar of the application. The library and it's files may be retrieved from 
http://sigar.hyperic.com/, or from the "Distribution Builds" folder in the repository structure.

Features that need to be developed before the first release candidate:

  - Control of the number of threads and Pinger runnables working on the list of targets. Could be done automatically, by determining the system's capabilities via the sigar library, or via a user-defined preference setting, or BOTH; automatically, with the possibility of overriding the detected system capabilities.
  - Proper target list manipulation, without causing concurrency or monitor exceptions.
