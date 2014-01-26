#Ben Below 2014

file = open("page1.txt","r")
outfile = open("wanted.txt","r+")
stringlist = []
first = True
for i in file:
    #line at a time
    i = i.split("\t")
    stringlist.append(i)
#stringlist = list of lists, each is one line separated by tab

index = stringlist[0][0]
linenumber = len(stringlist)
for i in range(0,linenumber):
    print("\n")
    print(stringlist[i][2])
    isGood = raw_input("Do you want this? y or n: ")
    print(isGood)
    if isGood == "y":
        for item in stringlist[i]:
            outfile.write(item)
        
    elif isGood == "q":
        break
    else:
        pass

file.close()
outfile.close()
#waiting = input("done")
