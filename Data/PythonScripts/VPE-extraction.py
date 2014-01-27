#Ben Below 2014
import os

home = os.path.expanduser("~")
print(home)

#path varaibles - change to make script work over different computers. named by file extension
annLoc = home + "\Dropbox\VPE-Ben (1)\JohanBos-VPECorpus\wsj"
posFolderLoc = home + "\Dropbox\VPE-Ben (1)\WSJ"
results = open("VPE_Results.txt","w")
#note - when opening files, must include a "\/" first when concatenating to Loc variables

globalMatched = 0
globalChecked = 0
globalMissing = 0

folder00 = posFolderLoc + "\/00"

#file = open(home + "\Dropbox\VPE-Ben\WSJ\/00\WSJ_0018.POS")

def removeLast(word,char):
    "removes last instance of char within word"
    word = word[::-1]
    if char not in word:
        return word[::-1]
    index = word.index(char)
    newword = word[:index] + word[index+1:]
    return newword[::-1]

def getSentences(file):
    "Returns a list of sentences from a .pos file. Removes all tags and square brackets"
    tempstring = ""
    sentences = []
    isTag = False
    isEquals = False
    isQuote = False
    isAfterQuote = False
    isOpenBracket = False
    openQuote = False #tracks whether opening or closing quote marks
    for item in file:
        for token in item:
            if isQuote and token not in ("`","'","/"):                                   #puts words with apostrophes back together
                tempstring = removeLast(tempstring," ") 
                isQuote = False
            if isEquals:
                if token != "=":                                        #end of sentence delimiter
                    isEquals = False
                    sentences.append(tempstring)
                    tempstring = ""
            if token == "]":
                isTag = False
            elif token == "[":
                isTag = False
                isOpenBracket = True                                    #open bracket always has an extra space after
            elif token == "/":                                      #removes tags
                isTag = True
            elif token == " ":
                if isOpenBracket:   #remove bracket after open bracket
                    isOpenBracket = False
                elif isAfterQuote:
                    isAfterQuote = False
                elif isTag: #end of tag
                    isTag = False
                    tempstring += token
            elif token == "\n":
                pass
            elif token == "=":
                isEquals = True
            elif token in "," and not isTag:
                tempstring += token
                tempstring = removeLast(tempstring, " ")
            elif token == "." and tempstring[-1] == " ":
                tempstring += token
                tempstring = removeLast(tempstring, " ")
            elif (token == "'" or token == "`") and not isTag:
                if isQuote: #check if two quotes in a row -> double quote
                    tempstring = tempstring[:-1]
                    tempstring += '"'
                    openQuote = not openQuote
                    if openQuote:
                        isAfterQuote = True
                    else:
                        if len(tempstring) > 3 and tempstring[-3] in (",","."):
                            tempstring = removeLast(tempstring, " ")
                    isQuote = False 
                else:
                    isQuote = True
                    tempstring += token
                
                
            elif not isTag:
                tempstring += token
    sentences.append(tempstring)
    return sentences

class AnnFile():
    "Class for data in .ann files. Contains list of wsj parts, list of lists of 4x numerical parameters, list of tags, \
    list of sentences. Lists should all be same length, with same index in each = same sentence"
    def __init__(self,annfile):
        self.wsjlist = []
        self.paramlist = [] #list of lists, 4 param per line
        self.taglist = []
        self.sentencelist = []
        for line in annfile.readlines():
            param = []
            paramnum = 0
            tagnum = 0
            tag = []
            sentence = ""
            inSentence = False
            for part in line.split(" "):
                if part[0:3] == "wsj":      #wsj part
                    self.wsjlist.append(part)
                    paramnum += 1
                elif paramnum > 4:          #end of number parameters
                    paramnum = 0
                    tagnum += 1
                    tag.append(part)
                    inSentence = True
                elif paramnum != 0:         #number parameters
                    param.append(part)
                    paramnum += 1
                elif tagnum > 3:
                    inSentence = True
                    sentence += part + " "    
                elif tagnum != 0:
                    tag.append(part)
                    tagnum += 1
                
                elif inSentence:
                    sentence+= part + " "
                
            self.sentencelist.append(sentence)
            self.paramlist.append(param)
            self.taglist.append(tag)
    def printstuff(self):
        print(self.wsjlist)
        print(self.paramlist)
        print(self.taglist)
        print(self.sentencelist)
            
def extract_results(annfile,folder,output):
    "Will extract the appropriate lines from annfile, corresponding to the .pos files in folder, and write to output. \
    annfile = of type AnnFile. folder = path as string. output = file"
    global globalMatched,globalChecked, globalMissing
    matched = 0
    for index in range(0,len(annfile.sentencelist)):
        bMatched = False #boolean - whether we matched this line or not
        sMatch = annfile.wsjlist[index]
        if sMatch == "":
            print("Something went wrong in reading .ann, tring to match empty string")
            continue
        try:
            posfile = open(folder + "\/" + annfile.wsjlist[index].upper() + ".POS")
        except IOError:
            print("The file ", annfile.wsjlist[index].upper() + ".POS", "Doesn't Exist")
            globalMissing += 1
            continue
        thissentence = annfile.sentencelist[index]
        while thissentence[0] == ".": #trim leading .s
            thissentence = thissentence[1:]
        while thissentence[-1] in ("."," ","\n"): #trim trailing charcters not in string 
            thissentence = thissentence[:-2]
        tomatch= thissentence
        #print(index, ": \n")
        #print("Finding ''", tomatch, "''", "\n")
        for line in getSentences(posfile):
            if tomatch in line:
                matched += 1
                bMatched = True
                #print(line, "\n")
                output.write(line + "\n")
        if not bMatched:
            print("Couldn't match '", tomatch,"' in ", annfile.wsjlist[index].upper() + ".POS")
        posfile.close()
    print(folder[-2:],": Matched ", matched, " of ", len(annfile.sentencelist))
    globalMatched += matched
    globalChecked += len(annfile.sentencelist)
 
#annfile = open(annLoc + "\/00.ann")
#newann = AnnFile(annfile)
#newann.printstuff()
#extract_results(newann,folder00,results)

for annfile in os.listdir(annLoc):
    number = annfile[0:2]
    print(number)
    thisAnnFile = AnnFile(open(annLoc + "\/" + annfile))
    thisFolder = posFolderLoc + "\/" + str(number)
    extract_results(thisAnnFile,thisFolder,results)
    #thisAnnFile.printstuff() #prints all data extracted from .ann file
        
results.close()
print("Overall: Matched ", globalMatched, " of ", globalChecked-globalMissing)
print(globalMissing, " files were missing!")

