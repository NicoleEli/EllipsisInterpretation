file = open("NPE-C.txt","r")
outfile = open("NPE-testing.txt","r+")
stringlist = []
for i in file:
    #line at a time
    i = i.split("\t")
    stringlist.append(i)
#stringlist = list of lists, each is one line separated by tab

linenumber = len(stringlist)
print(linenumber)
for i in range(0,linenumber):
    outfile.write(stringlist[i][2].strip("\n")+" -- NPE-C\n")

file.close()
outfile.close()
#waiting = input("done")
