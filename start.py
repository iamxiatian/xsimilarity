#!/usr/bin/python
import os, sys, re

#home to store project
HOME = './'

#loop directory and get all libraries
def getlibraries(HOME):
	jars = ''
        split_char = ':'
        if os.name=='nt':
	        split_char = ';'
		jars = "."
	
        jars = jars + split_char + home +  '/target/classes/main';
        jars = jars + split_char + home + '/target/classes/test';
        jars = jars + split_char + home + '/src/main/resources';
        jars = jars + split_char + home + '/src/main/java';
        libdir = home + "/lib";

        for jar in os.listdir(libdir):
            if(jar==".svn"):continue
            fullname = os.path.join(libdir,jar)
            if os.path.isdir(fullname):
                for subjar in os.listdir(fullname):
                    if subjar.endswith('.jar'):
                        jars = jars + split_char + os.path.join(fullname, subjar)
            else:
                jars = jars + split_char + fullname    

        return jars;

home = os.getcwd()
if(os.path.basename(home)=='bin'):
        home = os.path.join(home,'..')
        
libpath = getlibraries(home)
command = 'java -Xmx256M -cp "' + libpath + '" '

if(len(sys.argv)==1):
	print "useage:./start.py runclass"
	command = command + ' ruc.irm.similarity.MainUI'
else:
    args = sys.argv
    for i in range(1,len(args)):
        command = command + ' ' + args[i]

print "execute ", command
print "xiatian@ruc."
print os.getcwd()
os.system(command)
