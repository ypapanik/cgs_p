# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.7

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/anithagenilos/Λήψεις/ph/warplda-master

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/anithagenilos/Λήψεις/ph/warplda-master/release

# Include any dependencies generated for this target.
include src/CMakeFiles/format.dir/depend.make

# Include the progress variables for this target.
include src/CMakeFiles/format.dir/progress.make

# Include the compile flags for this target's objects.
include src/CMakeFiles/format.dir/flags.make

src/CMakeFiles/format.dir/format.cpp.o: src/CMakeFiles/format.dir/flags.make
src/CMakeFiles/format.dir/format.cpp.o: ../src/format.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object src/CMakeFiles/format.dir/format.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/format.dir/format.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/format.cpp

src/CMakeFiles/format.dir/format.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/format.dir/format.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/format.cpp > CMakeFiles/format.dir/format.cpp.i

src/CMakeFiles/format.dir/format.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/format.dir/format.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/format.cpp -o CMakeFiles/format.dir/format.cpp.s

src/CMakeFiles/format.dir/format.cpp.o.requires:

.PHONY : src/CMakeFiles/format.dir/format.cpp.o.requires

src/CMakeFiles/format.dir/format.cpp.o.provides: src/CMakeFiles/format.dir/format.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/format.dir/build.make src/CMakeFiles/format.dir/format.cpp.o.provides.build
.PHONY : src/CMakeFiles/format.dir/format.cpp.o.provides

src/CMakeFiles/format.dir/format.cpp.o.provides.build: src/CMakeFiles/format.dir/format.cpp.o


# Object files for target format
format_OBJECTS = \
"CMakeFiles/format.dir/format.cpp.o"

# External object files for target format
format_EXTERNAL_OBJECTS =

src/format: src/CMakeFiles/format.dir/format.cpp.o
src/format: src/CMakeFiles/format.dir/build.make
src/format: src/libcommon.a
src/format: gflags/libgflags_nothreads.a
src/format: src/CMakeFiles/format.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable format"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/format.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
src/CMakeFiles/format.dir/build: src/format

.PHONY : src/CMakeFiles/format.dir/build

src/CMakeFiles/format.dir/requires: src/CMakeFiles/format.dir/format.cpp.o.requires

.PHONY : src/CMakeFiles/format.dir/requires

src/CMakeFiles/format.dir/clean:
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && $(CMAKE_COMMAND) -P CMakeFiles/format.dir/cmake_clean.cmake
.PHONY : src/CMakeFiles/format.dir/clean

src/CMakeFiles/format.dir/depend:
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/anithagenilos/Λήψεις/ph/warplda-master /home/anithagenilos/Λήψεις/ph/warplda-master/src /home/anithagenilos/Λήψεις/ph/warplda-master/release /home/anithagenilos/Λήψεις/ph/warplda-master/release/src /home/anithagenilos/Λήψεις/ph/warplda-master/release/src/CMakeFiles/format.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : src/CMakeFiles/format.dir/depend

