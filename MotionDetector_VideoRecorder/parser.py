import sys

def parseObject(outputFile):
    objectList = set()
    with open(outputFile,'r') as fileObject:
        for line in fileObject:
            if '%' in line:
                object, percentage = line.split(":")
                objectList.add(object)
    with open(outputFile,'w') as outputObject:
        for object in objectList:
            outputObject.write(object+'\n')

if __name__ == '__main__':
    outputFile = 'output.txt'#sys.argv[1]
    parseObject(outputFile)
