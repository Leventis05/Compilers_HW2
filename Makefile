
TREE_PATH = ./jtb_generated/syntaxtree
VISITOR_PATH = ./jtb_generated/visitor
JTB_CLASSES = ./jtb_classes
JTB_DEP = :$(TREE_PATH):$(VISITOR_PATH)

PARSER_CLASSES = ./parser_classes
PARSER_PATH = ./parser
PARSER_DEP = jtb

MAIN_PATH = ./src
MAIN_CLASSES = ./main_classes
MAIN_DEP = parser


all: jtb parser main

jtb:
	javac -d $(JTB_CLASSES) \
	-cp "lib/*:$(JTB_DEP):." \
	$(VISITOR_PATH)/*.java \
	$(TREE_PATH)/*.java

parser: $(PARSER_DEP)
	javac -d $(PARSER_CLASSES) \
		  -cp "lib/*:$(JTB_CLASSES):." \
		  $(PARSER_PATH)/*.java

main: $(MAIN_DEP)
	javac -d $(MAIN_CLASSES) \
		  -cp "lib/*:$(JTB_CLASSES):$(PARSER_CLASSES):." \
		  $(MAIN_PATH)/*.java

run: all
	java -cp "lib/*:$(JTB_CLASSES):$(PARSER_CLASSES):$(MAIN_CLASSES):." \
		 miniJecker $(ARGS)
