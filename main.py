from kivy.uix.boxlayout import BoxLayout
from kivy.uix.stacklayout import StackLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.uix.tabbedpanel import TabbedPanelItem
from kivy.uix.tabbedpanel import TabbedPanel
from kivy.app import App
from kivy.uix.button import Button
from functools import partial
from kivy.uix.textinput import TextInput
from kivy.uix.label import Label
from time import sleep
import threading
import brain
from math import sqrt
from kivy.graphics import Color, Rectangle
from kivy.core.window import Window
from kivy.uix.widget import Widget
queue = brain.tieronequeue(3)
queue2 = brain.tiertwoqueue()
queue3 = brain.selectrandomquestion(10)
weightedplay = True
numtopics = brain.numtopics()


class ReadBox(BoxLayout):
    """
    This class demonstrates various techniques that can be used for binding to
    events. Although parts could me made more optimal, advanced Python concepts
    are avoided for the sake of readability and clarity.
    """

    def _keyboard_closed(self):
        print('My keyboard have been closed!')
        self._keyboard.unbind(on_key_down=self._on_keyboard_down)
        self._keyboard = None

    def _on_keyboard_down(self, keyboard, keycode, text, modifiers):
        if text == ' ':
            self.answer(keyboard)
    def __init__(self, **kwargs):
        super(ReadBox, self).__init__(**kwargs)
        self._keyboard =self._keyboard = Window.request_keyboard(
            self._keyboard_closed, self, 'text')
        self._keyboard.bind(on_key_down=self._on_keyboard_down)
        self.orientation = "horizontal"
        self.readbool = True
        self.padding = 5
        # We start with binding to a normal event. The only argument
        # passed to the callback is the object which we have bound to.
        self.label = Button(text="my man!", size_hint=(1, 0.9), background_normal="", background_color=(0, 0, 0, 1))
        self.label.bind(on_press=self.answer)
        self.label.bind(pos=self.updatelabelsize, size=self.updatelabelsize)
        self.answerbox = Label(text="Answer:", size_hint=(1, 0.1), color=(0, 0, 0, 1))
        self.boxone = BoxLayout(size_hint=(0.85, 1), padding=15)
        self.boxone.orientation = "vertical"
        with self.answerbox.canvas.before:
            Color(255, 255, 255)
            self.answerbox.rect = Rectangle(pos=self.answerbox.pos, size=self.answerbox.size)
            self.answerbox.bind(pos=self.updaterect, size=self.updaterect)
        self.boxone.add_widget(self.label)
        self.boxone.add_widget((self.answerbox))
        self.add_widget(self.boxone)
        self.anotherbox = BoxLayout(size_hint=(0.15, 1), orientation="vertical")
        self.status = BoxLayout(size_hint=(1, 0.3), orientation="vertical")
        self.statustext = Label(size_hint=(1, 0.53), color=(0, 0.75, 1, 1))
        self.statustext.bind(pos=self.updatestatussize, size=self.updatestatussize)
        self.status.add_widget(self.statustext)
        self.moreoptions = Button(text="More Options", size_hint=(1, 0.47), background_normal="",
                                  background_color=(0, 0.35, 1, 1), color=(1, 1, 1, 1))
        self.moreoptions.bind(on_press=self.moreoptionsmethod)
        self.status.add_widget(self.moreoptions)
        self.anotherbox.add_widget(self.status)
        self.emptyspace = Label(size_hint=(1, 0.1))
        self.anotherbox.add_widget(self.emptyspace)
        self.correctbuttons = StackLayout(size_hint=(1, 0.45), orientation='bt-lr', spacing=10)
        self.btn2 = Button(text="Right", size_hint=(1, 0.5), background_normal="", background_color=(0, 1, 0, 1),
                           color=(0, 0, 0, 1))
        self.btn = Button(text="Wrong", size_hint=(1, 0.5), background_normal="", background_color=(1, 0, 0, 1),
                          color=(0, 0, 0, 1))
        self.tossupinfo = Button(text="Tossup Info", size_hint=(1, 0.13))
        self.moreemptyspace = Label(size_hint=(1, 0.02))
        self.btn2.bind(on_press=self.correct)
        self.btn.bind(on_press=self.incorrect)
        self.correctbuttons.add_widget(self.btn)
        self.correctbuttons.add_widget(self.btn2)
        self.anotherbox.add_widget(self.correctbuttons)
        self.anotherbox.add_widget(self.moreemptyspace)
        self.anotherbox.add_widget(self.tossupinfo)
        self.add_widget(self.anotherbox)
        self.questionsright = 0
        self.questionswrong = 0

    def updatetopics(self):
        global queue
        global queue2
        global queue3
        global numtopics
        queue = brain.tieronequeue(3)
        queue2 = brain.tiertwoqueue()
        queue3 = brain.selectrandomquestion(10)
        numtopics = brain.numtopics()

    def answer(self, obj):
        global queue
        global queue2
        global queue3
        if len(queue) > 0:
            self.answerbox.text = queue[0][0][1]
            self.label.text = queue[0][0][0]
        elif len(queue2) > 0:
            self.answerbox.text = queue2[0][0][1]
            self.label.text = queue2[0][0][0]
        elif len(queue3) > 0:
            self.answerbox.text = queue3[0][0][1]
            self.label.text = queue3[0][0][0]
        else:
            print("idk man")
        self.answerbox.text_size = self.answerbox.size
        self.answerbox.font_size = 1.5 * sqrt(self.answerbox.width) * sqrt(sqrt(5 / (len(self.answerbox.text))))
        self.answerbox.halign = 'center'
        self.answerbox.valign = "center"
        self.readbool = False
        self.label.text_size = self.label.size
        self.label.font_size = 0.035 * sqrt(self.label.height * self.label.width)
        self.label.halign = 'left'
        self.label.valign = "top"
        try:
            self.anotherbox.add_widget(self.correctbuttons)
            self.anotherbox.add_widget(self.moreemptyspace)
            self.anotherbox.add_widget(self.tossupinfo)
            self.status.add_widget(self.moreoptions)

        except:
            pass

    def moreoptionsmethod(self, obj):
        self.remove_widget(self.anotherbox)
        self.remove_widget(self.boxone)
        self.box = FloatLayout(size_hint=(1, 1))
        self.btn7 = Button(text="Add or Remove Topics", size_hint=(0.4, 0.4), pos_hint={'x': 0.55, 'y': 0.55})
        self.btn6 = Button(text="Change Settings", size_hint=(0.4, 0.4), pos_hint={'x': 0.55, 'y': 0.05})
        self.btn5 = Button(text="Statistics", size_hint=(0.4, 0.4), pos_hint={'x': 0.05, 'y': 0.55})
        self.btn4 = Button(text="Back", size_hint=(0.4, 0.4), pos_hint={'x': 0.055, 'y': 0.055})
        self.btn7.bind(on_press=self.changetopics)
        self.btn4.bind(on_press=self.back)
        self.box.add_widget(self.btn7)
        self.box.add_widget(self.btn6)
        self.box.add_widget(self.btn5)
        self.box.add_widget(self.btn4)
        self.add_widget(self.box)

    def changetopics(self, obj):
        self.clear_widgets()
        self.orientation = 'vertical'
        space = Label(size_hint=(1, 0.02))
        self.inputtext = TextInput(hint_text='Add Topics, one comma separating each topic.', size_hint=(0.95, 0.25),
                                   pos_hint={'x': 0.025})
        moreemptyspace = Label(size_hint=(1, 0.07))
        addtopicsbutton = Button(text="Add Topics!", size_hint=(0.4, 0.25), pos_hint={'x': 0.3})
        addtopicsbutton.bind(on_press=self.jsondumptopics)
        global numtopics
        self.evenmore = Label(size_hint=(1, 0.31), text=str(numtopics) + " Topics Available", font_size=18)
        backandclearbuttons = BoxLayout(size_hint=(1, 0.15))
        backbutton = Button(text="Back", size_hint=(0.2, 1))
        bottombuttonsspace = Label(size_hint=(0.6, 1))
        clearbutton = Button(text="Clear topics", size_hint=(0.2, 1), pos_hint={'x': 0.79})
        backbutton.bind(on_press=self.back)
        clearbutton.bind(on_press=self.cleartopics)
        backandclearbuttons.add_widget(backbutton)
        backandclearbuttons.add_widget(bottombuttonsspace)
        backandclearbuttons.add_widget(clearbutton)
        lst = [space, self.inputtext, moreemptyspace, addtopicsbutton, self.evenmore, backandclearbuttons]
        for element in lst:
            self.add_widget(element)

    def jsondumptopics(self, obj):
        txt = self.inputtext.text
        topics = txt.split(',')
        self.evenmore.halign = 'center'
        self.evenmore.valign = 'center'
        self.evenmore.text = "Adding " + str(len(topics)) + " topics. May take a while..."
        self.evenmore.text_size = self.evenmore.size
        self.evenmore.font_size = 18
        a = threading.Thread(target=self.jsondumphelper, daemon=True)
        a.start()

    def jsondumphelper(self):
        txt = self.inputtext.text
        topics = txt.split(',')
        bruh = []
        for topic in topics:
            bruh.append(topic.strip().lower())
        x = 0
        for topic in bruh:
            try:
                brain.jsondump(topic)
                x = x + 1
            except:
                print('failed!')
        self.evenmore.text = "Added " + str(x) + "/" + str(len(topics)) + " topics! Dropped " + str(
            len(topics) - x) + " topics. \n Topics are dropped because zero tossups were found, or quizdb is tripping"
        self.evenmore.text_size = self.evenmore.size
        self.updatetopics()
        sleep(5)
        self.evenmore.text = str(numtopics) + " Topics Available"

    def cleartopics(self, obj):
        brain.cleartopics()
        if brain.numtopics() == 0:
            self.evenmore.text = "Successfully cleared all topics. No Going back lmao"
            self.evenmore.text_size = self.evenmore.size
            self.evenmore.font_size = 13
            global numtopics
            numtopics = 0
            self.start()
        else:
            self.evenmore.text = "Failed to clear topics for some dumb reason."
            self.evenmore.text_size = self.evenmore.size
            self.evenmore.font_size = 13

    def back(self, obj):
        self.clear_widgets()
        self.orientation = 'horizontal'
        self.add_widget(self.boxone)
        self.add_widget(self.anotherbox)
        self.start()

    def correct(self, obj):  # updates queue
        global queue
        global queue2
        global queue3
        if len(queue) > 0:
            brain.correct(queue[0][1])
            del queue[0]
            self.read()
        elif len(queue2) > 0:
            brain.correct(queue2[0][1])
            del queue2[0]
            self.read()
        elif len(queue3) > 0:
            brain.correct(queue3[0][1])
            del queue3[0]
            self.read()
        else:
            print("idk man")

        self.questionsright = self.questionsright + 1
        self.updatestatus()

    def incorrect(self, obj):
        global queue
        global queue2
        global queue3
        if len(queue) > 0:
            brain.wrong(queue[0][1])
            queue.append(brain.selectquestions(brain.parsetopic(queue[0][1]), 1)[0])
            del queue[0]
            self.read()
        elif len(queue2) > 0:
            brain.wrong(queue2[0][1])
            del queue2[0]
            self.read()
        elif len(queue3) > 0:
            brain.wrong(queue3[0][1])
            del queue3[0]
            self.read()
        else:
            print("idk man")
        self.questionswrong = self.questionswrong + 1
        self.updatestatus()

    def updatelabel(self):  # outputs text to screen
        global queue
        global queue2
        global queue3
        sleep(0.01)
        # if len(queue) > 0:
        #     text = brain.split_into_sentences(queue[0][0][0])
        # elif len(queue2) > 0:
        #     text = brain.split_into_sentences(queue2[0][0][0])
        # elif len(queue3) == 1:
        #     self.updatetierthree()
        #     text = brain.split_into_sentences(queue3[0][0][0])
        # else:
        #     text = brain.split_into_sentences(queue3[0][0][0])
        if len(queue) > 0:
            text = queue[0][0][0].split()
        elif len(queue2) > 0:
            text = queue2[0][0][0].split()
        elif len(queue3) == 1:
            self.updatetierthree()
            text = queue3[0][0][0].split()
        else:
            text = queue3[0][0][0].split()
        x = 0
        # self.label.text = queue[0][0][0]
        self.label.text_size = self.label.size
        print(self.label.height, self.label.width)
        self.label.font_size = 0.035 * sqrt(self.label.height * self.label.width)
        self.label.halign = 'left'
        self.label.valign = "top"
        self.label.text = ""
        while x < len(text):
            if x > 0:
                sleep(.2)
            if not self.readbool:
                break
            self.label.text = self.label.text + text[x] + " "
            x = x + 1
            print(x)
        if self.readbool:
            try:
                self.anotherbox.add_widget(self.tossupinfo)
                self.anotherbox.add_widget(self.moreemptyspace)
                self.status.add_widget(self.moreoptions)
                self.anotherbox.add_widget(self.correctbuttons)

            except:
                pass

    def updaterect(self, instance, value):
        self.answerbox.rect.pos = instance.pos
        self.answerbox.rect.size = instance.size

    def updatelabelsize(self, instance, value):
        self.label.text_size = self.label.size
        print(self.label.height, self.label.width)
        self.label.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.answerbox.text_size = self.answerbox.size
        self.answerbox.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.answerbox.halign = 'center'
        self.answerbox.valign = "center"
        self.label.halign = 'left'
        self.label.valign = "top"

    def updatestatussize(self, instance, value):
        self.updatestatus()

    def updatestatus(self):
        global queue
        if len(queue) > 0:
            self.statustext.text = "Tier one: " + str(len(queue)) + " tossups left"
        elif len(queue2) > 0:
            self.statustext.text = "Tier Two: " + str(len(queue2)) + " tossups left"
        elif weightedplay:
            self.statustext.text = "Tier Three: Weighted Play"
        else:
            self.statustext.text = "Tier Three: Normal Play"

        if self.questionswrong == 0 and self.questionsright == 0:
            self.statustext.text = "Status:\n" + self.statustext.text
        else:
            a = "\n" + str(self.questionsright) + "/" + str(self.questionswrong + self.questionsright) + " = " + str(
                int((100 * self.questionsright) / (self.questionsright + self.questionswrong))) + "%"
            self.statustext.text = "Status:\n" + self.statustext.text + a
            print(a)
        self.statustext.text_size = self.statustext.size
        self.statustext.font_size = self.statustext.width * 0.15
        self.statustext.halign = 'center'
        self.statustext.valign = 'top'

    def updatetierthree(self):
        global queue3
        global weightedplay
        if weightedplay:
            queue3 = brain.selectweightedquestion(10)
        else:
            queue3 = brain.selectrandomquestion(10)

    def read(self):
        self.readbool = True
        self.answerbox.text = "Answer:"
        self.answerbox.text_size = self.answerbox.size
        self.answerbox.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.anotherbox.remove_widget(self.correctbuttons)
        self.anotherbox.remove_widget(self.tossupinfo)
        self.anotherbox.remove_widget(self.moreemptyspace)
        self.status.remove_widget(self.moreoptions)
        thread = threading.Thread(target=self.updatelabel)
        thread.daemon = True
        thread.start()

    def start(self):
        global numtopics
        sleep(0.01)
        self.updatestatus()
        self.statustext.font_size = 13
        if numtopics > 1:
            self.read()
        else:
            self.clear_widgets()
            button = Button(
                text="There are no topics. Download Topics to play.\n If you try to play wuthout downloading at least TWO topics,\n the program will bork.")
            button.bind(on_press=self.changetopics)
            self.add_widget(button)

    def on_event1(self, obj):
        print("Typical event from", obj)

    def on_property(self, obj, value):
        print("Typical property change from", obj, "to", value)

    def on_anything(self, *args, **kwargs):
        print('The flexible function has *args of', str(args),
              "and **kwargs of", str(kwargs))

class MyKeyboardListener(Widget):

    def __init__(self, **kwargs):
        super(MyKeyboardListener, self).__init__(**kwargs)
        self._keyboard = Window.request_keyboard(
            self._keyboard_closed, self, 'text')
        if self._keyboard.widget:
            # If it exists, this widget is a VKeyboard object which you can use
            # to change the keyboard layout.
            pass
        self._keyboard.bind(on_key_down=self._on_keyboard_down)

    def _on_keyboard_down(self, keyboard, keycode, text, modifiers):


        # Return True to accept the key. Otherwise, it will be used by
        # the system.
        return True


class DemoApp(App):

    def build(self):
        self.title = "Brain Team!"
        y = ReadBox()

        y.start()
        return y


print(len(queue3))
print(len(queue3))
print(len(queue3))
print(len(queue3))
print(len(queue3))
print(len(queue3))
print(len(queue3))
if __name__ == "__main__":
    DemoApp().run()
