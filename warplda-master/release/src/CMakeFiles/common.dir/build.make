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
include src/CMakeFiles/common.dir/depend.make

# Include the progress variables for this target.
include src/CMakeFiles/common.dir/progress.make

# Include the compile flags for this target's objects.
include src/CMakeFiles/common.dir/flags.make

src/CMakeFiles/common.dir/Bigraph.cpp.o: src/CMakeFiles/common.dir/flags.make
src/CMakeFiles/common.dir/Bigraph.cpp.o: ../src/Bigraph.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object src/CMakeFiles/common.dir/Bigraph.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/common.dir/Bigraph.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/Bigraph.cpp

src/CMakeFiles/common.dir/Bigraph.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/common.dir/Bigraph.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/Bigraph.cpp > CMakeFiles/common.dir/Bigraph.cpp.i

src/CMakeFiles/common.dir/Bigraph.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/common.dir/Bigraph.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/Bigraph.cpp -o CMakeFiles/common.dir/Bigraph.cpp.s

src/CMakeFiles/common.dir/Bigraph.cpp.o.requires:

.PHONY : src/CMakeFiles/common.dir/Bigraph.cpp.o.requires

src/CMakeFiles/common.dir/Bigraph.cpp.o.provides: src/CMakeFiles/common.dir/Bigraph.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/common.dir/build.make src/CMakeFiles/common.dir/Bigraph.cpp.o.provides.build
.PHONY : src/CMakeFiles/common.dir/Bigraph.cpp.o.provides

src/CMakeFiles/common.dir/Bigraph.cpp.o.provides.build: src/CMakeFiles/common.dir/Bigraph.cpp.o


src/CMakeFiles/common.dir/AdjList.cpp.o: src/CMakeFiles/common.dir/flags.make
src/CMakeFiles/common.dir/AdjList.cpp.o: ../src/AdjList.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object src/CMakeFiles/common.dir/AdjList.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/common.dir/AdjList.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/AdjList.cpp

src/CMakeFiles/common.dir/AdjList.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/common.dir/AdjList.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/AdjList.cpp > CMakeFiles/common.dir/AdjList.cpp.i

src/CMakeFiles/common.dir/AdjList.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/common.dir/AdjList.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/AdjList.cpp -o CMakeFiles/common.dir/AdjList.cpp.s

src/CMakeFiles/common.dir/AdjList.cpp.o.requires:

.PHONY : src/CMakeFiles/common.dir/AdjList.cpp.o.requires

src/CMakeFiles/common.dir/AdjList.cpp.o.provides: src/CMakeFiles/common.dir/AdjList.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/common.dir/build.make src/CMakeFiles/common.dir/AdjList.cpp.o.provides.build
.PHONY : src/CMakeFiles/common.dir/AdjList.cpp.o.provides

src/CMakeFiles/common.dir/AdjList.cpp.o.provides.build: src/CMakeFiles/common.dir/AdjList.cpp.o


src/CMakeFiles/common.dir/Vocab.cpp.o: src/CMakeFiles/common.dir/flags.make
src/CMakeFiles/common.dir/Vocab.cpp.o: ../src/Vocab.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object src/CMakeFiles/common.dir/Vocab.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/common.dir/Vocab.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/Vocab.cpp

src/CMakeFiles/common.dir/Vocab.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/common.dir/Vocab.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/Vocab.cpp > CMakeFiles/common.dir/Vocab.cpp.i

src/CMakeFiles/common.dir/Vocab.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/common.dir/Vocab.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/Vocab.cpp -o CMakeFiles/common.dir/Vocab.cpp.s

src/CMakeFiles/common.dir/Vocab.cpp.o.requires:

.PHONY : src/CMakeFiles/common.dir/Vocab.cpp.o.requires

src/CMakeFiles/common.dir/Vocab.cpp.o.provides: src/CMakeFiles/common.dir/Vocab.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/common.dir/build.make src/CMakeFiles/common.dir/Vocab.cpp.o.provides.build
.PHONY : src/CMakeFiles/common.dir/Vocab.cpp.o.provides

src/CMakeFiles/common.dir/Vocab.cpp.o.provides.build: src/CMakeFiles/common.dir/Vocab.cpp.o


src/CMakeFiles/common.dir/NumaArray.cpp.o: src/CMakeFiles/common.dir/flags.make
src/CMakeFiles/common.dir/NumaArray.cpp.o: ../src/NumaArray.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object src/CMakeFiles/common.dir/NumaArray.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/common.dir/NumaArray.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/NumaArray.cpp

src/CMakeFiles/common.dir/NumaArray.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/common.dir/NumaArray.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/NumaArray.cpp > CMakeFiles/common.dir/NumaArray.cpp.i

src/CMakeFiles/common.dir/NumaArray.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/common.dir/NumaArray.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/NumaArray.cpp -o CMakeFiles/common.dir/NumaArray.cpp.s

src/CMakeFiles/common.dir/NumaArray.cpp.o.requires:

.PHONY : src/CMakeFiles/common.dir/NumaArray.cpp.o.requires

src/CMakeFiles/common.dir/NumaArray.cpp.o.provides: src/CMakeFiles/common.dir/NumaArray.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/common.dir/build.make src/CMakeFiles/common.dir/NumaArray.cpp.o.provides.build
.PHONY : src/CMakeFiles/common.dir/NumaArray.cpp.o.provides

src/CMakeFiles/common.dir/NumaArray.cpp.o.provides.build: src/CMakeFiles/common.dir/NumaArray.cpp.o


src/CMakeFiles/common.dir/clock.cpp.o: src/CMakeFiles/common.dir/flags.make
src/CMakeFiles/common.dir/clock.cpp.o: ../src/clock.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object src/CMakeFiles/common.dir/clock.cpp.o"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/common.dir/clock.cpp.o -c /home/anithagenilos/Λήψεις/ph/warplda-master/src/clock.cpp

src/CMakeFiles/common.dir/clock.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/common.dir/clock.cpp.i"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/anithagenilos/Λήψεις/ph/warplda-master/src/clock.cpp > CMakeFiles/common.dir/clock.cpp.i

src/CMakeFiles/common.dir/clock.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/common.dir/clock.cpp.s"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/anithagenilos/Λήψεις/ph/warplda-master/src/clock.cpp -o CMakeFiles/common.dir/clock.cpp.s

src/CMakeFiles/common.dir/clock.cpp.o.requires:

.PHONY : src/CMakeFiles/common.dir/clock.cpp.o.requires

src/CMakeFiles/common.dir/clock.cpp.o.provides: src/CMakeFiles/common.dir/clock.cpp.o.requires
	$(MAKE) -f src/CMakeFiles/common.dir/build.make src/CMakeFiles/common.dir/clock.cpp.o.provides.build
.PHONY : src/CMakeFiles/common.dir/clock.cpp.o.provides

src/CMakeFiles/common.dir/clock.cpp.o.provides.build: src/CMakeFiles/common.dir/clock.cpp.o


# Object files for target common
common_OBJECTS = \
"CMakeFiles/common.dir/Bigraph.cpp.o" \
"CMakeFiles/common.dir/AdjList.cpp.o" \
"CMakeFiles/common.dir/Vocab.cpp.o" \
"CMakeFiles/common.dir/NumaArray.cpp.o" \
"CMakeFiles/common.dir/clock.cpp.o"

# External object files for target common
common_EXTERNAL_OBJECTS =

src/libcommon.a: src/CMakeFiles/common.dir/Bigraph.cpp.o
src/libcommon.a: src/CMakeFiles/common.dir/AdjList.cpp.o
src/libcommon.a: src/CMakeFiles/common.dir/Vocab.cpp.o
src/libcommon.a: src/CMakeFiles/common.dir/NumaArray.cpp.o
src/libcommon.a: src/CMakeFiles/common.dir/clock.cpp.o
src/libcommon.a: src/CMakeFiles/common.dir/build.make
src/libcommon.a: src/CMakeFiles/common.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/anithagenilos/Λήψεις/ph/warplda-master/release/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Linking CXX static library libcommon.a"
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && $(CMAKE_COMMAND) -P CMakeFiles/common.dir/cmake_clean_target.cmake
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/common.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
src/CMakeFiles/common.dir/build: src/libcommon.a

.PHONY : src/CMakeFiles/common.dir/build

src/CMakeFiles/common.dir/requires: src/CMakeFiles/common.dir/Bigraph.cpp.o.requires
src/CMakeFiles/common.dir/requires: src/CMakeFiles/common.dir/AdjList.cpp.o.requires
src/CMakeFiles/common.dir/requires: src/CMakeFiles/common.dir/Vocab.cpp.o.requires
src/CMakeFiles/common.dir/requires: src/CMakeFiles/common.dir/NumaArray.cpp.o.requires
src/CMakeFiles/common.dir/requires: src/CMakeFiles/common.dir/clock.cpp.o.requires

.PHONY : src/CMakeFiles/common.dir/requires

src/CMakeFiles/common.dir/clean:
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release/src && $(CMAKE_COMMAND) -P CMakeFiles/common.dir/cmake_clean.cmake
.PHONY : src/CMakeFiles/common.dir/clean

src/CMakeFiles/common.dir/depend:
	cd /home/anithagenilos/Λήψεις/ph/warplda-master/release && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/anithagenilos/Λήψεις/ph/warplda-master /home/anithagenilos/Λήψεις/ph/warplda-master/src /home/anithagenilos/Λήψεις/ph/warplda-master/release /home/anithagenilos/Λήψεις/ph/warplda-master/release/src /home/anithagenilos/Λήψεις/ph/warplda-master/release/src/CMakeFiles/common.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : src/CMakeFiles/common.dir/depend

