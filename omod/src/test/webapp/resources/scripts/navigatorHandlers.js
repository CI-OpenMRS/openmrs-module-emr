describe("Tests for simple form navigation handlers", function() {

    describe("Keyboard handlers", function() {
        var fieldsKeyboardHandler, questionsKeyboardHandler;

        describe("Fields Keyboard handler", function() {
            var firstField, secondField, thirdField;
            beforeEach(function() {
                firstField = {isSelected: false, isValid: false, toggleSelection: '', select: ''};
                secondField = {isSelected: false, isValid: false, toggleSelection: ''};
                thirdField = {isSelected: false, isValid: false, toggleSelection: ''};
                questionsKeyboardHandler = jasmine.createSpyObj('questionsHandler',
                        ['handleUpKey', 'handleDownKey', 'selectedQuestion']);
                fieldsKeyboardHandler = new FieldsKeyboardHandler([firstField, secondField, thirdField], questionsKeyboardHandler);
            });

            it("should delegate up key handling if no selected field", function() {
                questionsKeyboardHandler.handleUpKey.andReturn(true);

                var wasHandled = fieldsKeyboardHandler.handleUpKey();

                expect(questionsKeyboardHandler.handleUpKey).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should not handle up key if there is a selected field", function() {
                secondField.isSelected = true;

                var wasHandled = fieldsKeyboardHandler.handleUpKey();

                expect(wasHandled).toBe(false);
            });

            it("should delegate down key handling if no selected field", function() {
                firstField.isSelected = false; secondField.isSelected = false;
                questionsKeyboardHandler.handleDownKey.andReturn(true);

                var wasHandled = fieldsKeyboardHandler.handleDownKey();

                expect(questionsKeyboardHandler.handleDownKey).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should not handle down key if there is a selected field", function() {
                secondField.isSelected = true;

                var wasHandled = fieldsKeyboardHandler.handleDownKey();

                expect(wasHandled).toBe(false);
            });

            it("should switch selection to next field within same question", function() {
                spyOn(firstField, 'isValid').andReturn(true);
                spyOn(firstField, 'toggleSelection');
                spyOn(secondField, 'toggleSelection');
                firstField.isSelected = true;
                questionsKeyboardHandler.selectedQuestion.andReturn({fields: [firstField, secondField]});

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
            });
            it("should switch selection to next field within different question", function() {
                spyOn(firstField, 'isValid').andReturn(true);
                spyOn(firstField, 'toggleSelection');
                spyOn(secondField, 'toggleSelection');
                firstField.isSelected = true;

                var firstQuestion = jasmine.createSpyObj('firstQuestion', ['toggleSelection']);
                firstQuestion.fields = [firstQuestion]; firstField.parentQuestion = firstQuestion;
                var secondQuestion = jasmine.createSpyObj('secondQuestion', ['toggleSelection']);
                secondQuestion.fields = [secondQuestion]; secondField.parentQuestion = secondQuestion;
                questionsKeyboardHandler.selectedQuestion.andReturn(firstQuestion);

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to next field if current field is invalid", function() {
                firstField.isSelected = true;
                spyOn(firstField, 'isValid').andReturn(false);
                spyOn(firstField, 'select');

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(firstField.select).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });

            it("should switch selection to previous field within same question", function() {
                spyOn(firstField, 'toggleSelection');
                spyOn(secondField, 'toggleSelection');
                secondField.isSelected = true;
                questionsKeyboardHandler.selectedQuestion.andReturn({fields: [firstField, secondField]});

                var wasHandled = fieldsKeyboardHandler.handleShiftTabKey();

                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(firstField.toggleSelection).toHaveBeenCalled();
            });
            it("should switch selection to next field within different question", function() {
                spyOn(secondField, 'toggleSelection');
                spyOn(thirdField, 'toggleSelection');
                thirdField.isSelected = true;

                var firstQuestion = jasmine.createSpyObj('firstQuestion', ['toggleSelection']);
                firstQuestion.fields = [firstField, secondField];
                firstField.parentQuestion = firstQuestion;
                secondField.parentQuestion = firstQuestion;
                var secondQuestion = jasmine.createSpyObj('secondQuestion', ['toggleSelection']);
                secondQuestion.fields = [thirdField];
                thirdField.parentQuestion = secondQuestion;
                questionsKeyboardHandler.selectedQuestion.andReturn(secondQuestion);

                var wasHandled = fieldsKeyboardHandler.handleShiftTabKey();

                expect(thirdField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
            });
        });

        describe("Questions Keyboard handler", function() {
            var firstQuestion, secondQuestion;
            beforeEach(function() {
                firstQuestion = jasmine.createSpyObj('firstQuestion', ['toggleSelection']);
                secondQuestion = jasmine.createSpyObj('secondQuestion', ['toggleSelection']);
                questionsKeyboardHandler = new QuestionsKeyboardHandler([firstQuestion, secondQuestion]);
            });

            it("should switch selection to next question within same section", function() {
                firstQuestion.isSelected = true; secondQuestion.isSelected = false;
                firstQuestion.isValid = ''; spyOn(firstQuestion, 'isValid').andReturn(true);

                var wasHandled = questionsKeyboardHandler.handleDownKey();

                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should switch selection to next question within different section", function() {
                firstQuestion.isSelected = true; secondQuestion.isSelected = false;
                firstQuestion.isValid = ''; spyOn(firstQuestion, 'isValid').andReturn(true);

                var firstSection = jasmine.createSpyObj('firstSection', ['toggleSelection']);
                var secondSection = jasmine.createSpyObj('secondSection', ['toggleSelection'])
                firstSection.questions = [firstQuestion]; secondSection.questions = [secondQuestion];
                firstQuestion.parentSection = firstSection; secondQuestion.parentSection = secondSection;

                questionsKeyboardHandler = new QuestionsKeyboardHandler([firstQuestion, secondQuestion]);
                var wasHandled = questionsKeyboardHandler.handleDownKey();

                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(firstSection.toggleSelection).toHaveBeenCalled();
                expect(secondSection.toggleSelection).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should not switch selection to next question if question is not valid", function() {
                var firstQuestion = jasmine.createSpyObj('firstQuestion', ['isValid']);
                var secondQuestion = {isSelected: false};
                firstQuestion.isSelected = true;
                firstQuestion.isValid.andReturn(false);

                questionsKeyboardHandler = new QuestionsKeyboardHandler([firstQuestion, secondQuestion]);
                var wasHandled = questionsKeyboardHandler.handleDownKey();

                expect(wasHandled).toBe(true);
            });

            it("should switch selection to previous question within same section", function() {
                firstQuestion.isSelected = false; secondQuestion.isSelected = true;

                var wasHandled = questionsKeyboardHandler.handleUpKey();

                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should switch selection to previous question within different section", function() {
                firstQuestion.isSelected = false; secondQuestion.isSelected = true;

                var firstSection = jasmine.createSpyObj('firstSection', ['toggleSelection']);
                var secondSection = jasmine.createSpyObj('secondSection', ['toggleSelection'])
                firstSection.questions = [firstQuestion]; secondSection.questions = [secondQuestion];
                firstQuestion.parentSection = firstSection; secondQuestion.parentSection = secondSection;

                questionsKeyboardHandler = new QuestionsKeyboardHandler([firstQuestion, secondQuestion]);
                var wasHandled = questionsKeyboardHandler.handleUpKey();

                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(firstSection.toggleSelection).toHaveBeenCalled();
                expect(secondSection.toggleSelection).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
        });
    });

    describe("Mouse handlers", function() {

        describe("Section mouse handlers", function() {
            var sectionsMouseHandler, firstSection, secondSection, thirdSection, question, field;
            beforeEach(function() {
                firstSection = {isSelected: false, toggleSelection: '', isValid: '', title: $('<li></li>')};
                secondSection = {isSelected: false, toggleSelection: '', isValid: '', title: $('<li></li>')};
                thirdSection = {isSelected: false, toggleSelection: '', isValid: '', title: $('<li></li>')};

                field = {isSelected: true, toggleSelection: '', select: ''};
                question = {fields: [field], toggleSelection: '', isSelected: true};

                sectionsMouseHandler = new SectionMouseHandler([firstSection, secondSection, thirdSection]);
            });

            it("should switch selection to section ahead if current section is valid", function() {
                spyOn(firstSection, 'isValid').andReturn(true); spyOn(firstSection, 'toggleSelection');
                spyOn(secondSection, 'toggleSelection');
                spyOn(question, 'toggleSelection');
                spyOn(field, 'toggleSelection');
                firstSection.isSelected = true;
                secondSection.questions = [question];

                secondSection.title.click();

                expect(firstSection.toggleSelection).toHaveBeenCalled();
                expect(secondSection.toggleSelection).toHaveBeenCalled();
                expect(question.toggleSelection).toHaveBeenCalled();
                expect(field.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to section ahead if current section is not valid", function() {
                spyOn(firstSection, 'isValid').andReturn(false);
                spyOn(field, 'select');
                firstSection.isSelected = true;
                firstSection.questions = [question];

                secondSection.title.click();

                expect(field.select).toHaveBeenCalled();
            });
            it("should not switch selection to section ahead if section in between is not valid", function() {
                spyOn(firstSection, 'isValid').andReturn(true);
                spyOn(secondSection, 'isValid').andReturn(false);
                spyOn(field, 'select');
                firstSection.isSelected = true;
                firstSection.questions = [question];

                thirdSection.title.click();

                expect(field.select).toHaveBeenCalled();
            });
            it("should switch selection to section behind", function() {
                spyOn(firstSection, 'toggleSelection');
                spyOn(secondSection, 'toggleSelection');
                spyOn(question, 'toggleSelection');
                spyOn(field, 'toggleSelection');
                secondSection.isSelected = true;
                firstSection.questions = [question];

                firstSection.title.click();

                expect(firstSection.toggleSelection).toHaveBeenCalled();
                expect(secondSection.toggleSelection).toHaveBeenCalled();
                expect(question.toggleSelection).toHaveBeenCalled();
                expect(field.toggleSelection).toHaveBeenCalled();
            });
        });

        describe("Question mouse handlers", function() {
           var questionsMouseHandler, firstQuestion, secondQuestion, thirdQuestion, field;
            beforeEach(function() {
                firstQuestion = {isSelected: false, toggleSelection: '', isValid: '', questionLi: $('<li></li>'), select: ''};
                secondQuestion = {isSelected: false, toggleSelection: '', isValid: '', questionLi: $('<li></li>')};
                thirdQuestion = {isSelected: false, questionLi: $('<li></li>')};

                field = {isSelected: false, toggleSelection: '', select: ''};

                questionsMouseHandler = new QuestionsMouseHandler([firstQuestion, secondQuestion, thirdQuestion]);
            });

            it("should switch selection to question ahead if current question is valid", function() {
                spyOn(firstQuestion, 'isValid').andReturn(true);
                spyOn(firstQuestion, 'toggleSelection');
                spyOn(secondQuestion, 'toggleSelection');
                spyOn(field, 'toggleSelection');
                firstQuestion.isSelected = true;
                secondQuestion.fields = [field];

                secondQuestion.questionLi.click();

                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to question ahead if current question is not valid", function() {
                spyOn(firstQuestion, 'isValid').andReturn(false);
                spyOn(field, 'select');
                firstQuestion.isSelected = true;
                field.isSelected = true;
                firstQuestion.fields = [field];

                secondQuestion.questionLi.click();

                expect(field.select).toHaveBeenCalled();
            });
            it("should not switch selection to question ahead if question in between is not valid", function() {
                spyOn(firstQuestion, 'isValid').andReturn(true);
                spyOn(secondQuestion, 'isValid').andReturn(false);
                spyOn(field, 'select');
                firstQuestion.isSelected=true;
                field.isSelected = true;
                firstQuestion.fields = [field];

                thirdQuestion.questionLi.click();

                expect(field.select).toHaveBeenCalled();
            });
            it("should switch selection to question behind", function() {
                spyOn(secondQuestion, 'isValid').andReturn(true);
                spyOn(firstQuestion, 'toggleSelection');
                spyOn(secondQuestion, 'toggleSelection');
                spyOn(field, 'toggleSelection');
                secondQuestion.isSelected = true;
                firstQuestion.fields = [field];

                firstQuestion.questionLi.click();

                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
            });
        });

        describe("Fields mouse handlers", function() {
            var fieldsMouseHandler, firstField, secondField, thirdField;
            beforeEach(function() {
                firstField = {isSelected: false, toggleSelection: '', isValid: '', element: $('<input />'), select:''};
                secondField = {isSelected: false, toggleSelection: '', isValid: '', element: $('<input />')};
                thirdField = {isSelected: false, toggleSelection: '', element: $('<input />')};
                fieldsMouseHandler = new FieldsMouseHandler([firstField, secondField, thirdField]);
            });

            it("should switch selection to field ahead if current field is valid", function() {
                spyOn(firstField, 'isValid').andReturn(true);
                spyOn(firstField, 'toggleSelection');
                spyOn(secondField, 'toggleSelection');
                firstField.isSelected=true;

                secondField.element.mousedown();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to field ahead if current field is not valid", function() {
                spyOn(firstField, 'isValid').andReturn(false);
                spyOn(firstField, 'select');
                firstField.isSelected=true;

                secondField.element.mousedown();

                expect(firstField.select).toHaveBeenCalled();
            });
            it("should not switch selection to field ahead if field in between is not valid", function() {
                spyOn(firstField, 'isValid').andReturn(true);
                spyOn(secondField, 'isValid').andReturn(false);
                spyOn(firstField, 'select');
                firstField.isSelected=true;

                thirdField.element.mousedown();

                expect(firstField.select).toHaveBeenCalled();
            });
            it("should switch selection to field behind", function() {
                spyOn(firstField, 'toggleSelection');
                spyOn(secondField, 'toggleSelection');
                secondField.isSelected=true;

                firstField.element.mousedown();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
            });
        });
    });
})