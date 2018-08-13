from kivy.uix.boxlayout import BoxLayout
from kivy.uix.stacklayout import StackLayout

from kivy.app import App
from kivy.uix.button import Button
from kivy.graphics import Color, Rectangle
from functools import partial
from kivy.uix.textinput import TextInput
from kivy.uix.label import Label
from time import sleep
import threading
import brain
from math import sqrt
from kivy.graphics import Color, Rectangle


queue = brain.tieronequeue(2)
queue2 = brain.tiertwoqueue()
queue3 = brain.selectrandomquestion(50)
numtopics = brain.numtopics()
class ReadBox(BoxLayout):
    """
    This class demonstrates various techniques that can be used for binding to
    events. Although parts could me made more optimal, advanced Python concepts
    are avoided for the sake of readability and clarity.
    """
    def __init__(self, **kwargs):
        super(ReadBox, self).__init__(**kwargs)
        self.orientation = "horizontal"
        self.readbool = True
        self.padding = 5
        # We start with binding to a normal event. The only argument
        # passed to the callback is the object which we have bound to.
        self.label = Label(text="my man!", size_hint = (1,0.9),)
        self.label.bind(pos = self.updatelabelsize, size = self.updatelabelsize)
        self.answerbox = Label(text = "answer", size_hint = (1, 0.1), color = (0,0,0,1))
        boxone = BoxLayout(size_hint = (0.8,1))
        boxone.orientation = "vertical"
        with self.answerbox.canvas.before:
            Color(255, 255, 255)
            self.answerbox.rect = Rectangle(pos = self.answerbox.pos, size = self.answerbox.size)
            self.answerbox.bind(pos = self.updaterect, size = self.updaterect)
        boxone.add_widget(self.label)
        boxone.add_widget((self.answerbox))
        self.add_widget(boxone)
        self.anotherbox = BoxLayout(size_hint = (0.2,1), orientation = "vertical")
        self.correctbuttons = StackLayout(size_hint = (1,0.15), orientation = 'bt-lr', spacing = 20)
        self.btn2 = Button(text = "Right", size_hint = (1,0.5))
        self.btn = Button(text = "Wrong", size_hint = (1, 0.5))
        self.btn3 =Button(text = "Show Answer",size_hint = (1,0.5))
        self.btn2.bind(on_press = self.correct)
        self.btn3.bind(on_press = self.answer)
        self.btn.bind(on_press = self.incorrect)
        self.correctbuttons.add_widget(self.btn2)
        self.correctbuttons.add_widget(self.btn)
        self.anotherbox.add_widget(self.btn3)
        self.anotherbox.add_widget(self.correctbuttons)
        self.add_widget(self.anotherbox)
        
    def answer(self, obj):
        global queue
        self.answerbox.text = queue[0][0][1]
        self.answerbox.text_size = self.answerbox.size
        self.answerbox.font_size = 1.5*sqrt(self.answerbox.width) * sqrt(sqrt(5/(len(queue[0][0][1]))))
        self.answerbox.halign = 'center'
        self.answerbox.valign = "center"
        self.readbool = False
        self.label.text = queue[0][0][0]
        self.label.text_size = self.label.size
        self.label.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.label.halign = 'left'
        self.label.valign = "top"
        try:
            self.anotherbox.add_widget(self.btn)
            self.anotherbox.add_widget(self.btn2)
        except:
            pass
    def correct(self, obj): # updates queue
        global queue
        global queue2
        global queue3
        if len(queue) != 0:
            brain.correct(queue[0][1])
            del queue[0]
            self.read()
        elif len(queue2) != 0:
            brain.correct(queue2[0][1])
            del queue2[0]
            self.read()
        elif len(queue3) != 0:
            brain.correct(queue3[0][1])
            del queue3[0]
            self.read()
        else:
            print("idk man")
    def incorrect(self, obj):
        global queue
        brain.wrong(queue[0][1])
        queue.append(brain.selectquestions(brain.parsetopic(queue[0][1]), 1)[0])
        del queue[0]
        self.read()

    def updatelabel(self): #outputs text to screen
        global queue
        global queue2
        global queue3
        sleep(0.01)
        if len(queue) != 0:
            text = brain.split_into_sentences(queue[0][0][0])
        elif len(queue2) != 0:
            text = brain.split_into_sentences(queue2[0][0][0])
        elif len(queue3) != 0:
            text = brain.split_into_sentences(queue3[0][0][0])
        else:
            queue3 = brain.selectrandomquestion(50)
        x = 0
        #self.label.text = queue[0][0][0]
        self.label.text_size = self.label.size
        print(self.label.height, self.label.width)
        self.label.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.label.halign = 'left'
        self.label.valign = "top"
        self.label.text = ""
        while x < len(text):
            if not self.readbool:
                break
            self.label.text = self.label.text + text[x] + " "
            x = x + 1
            print(x)
            sleep(1)
        if self.readbool:
            try:
                self.anotherbox.add_widget(self.btn)
                self.anotherbox.add_widget(self.btn2)
            except:
                pass
    def updaterect(self,instance, value):
        self.answerbox.rect.pos = instance.pos
        self.answerbox.rect.size = instance.size

    def updatelabelsize(self, instance, value):
        self.label.text_size = self.label.size
        print(self.label.height, self.label.width)
        self.label.font_size = 0.04 * sqrt(self.label.height * self.label.width)
        self.answerbox.text_size = self.answerbox.size
        self.answerbox.font_size = 1.5 * sqrt(self.answerbox.width) * sqrt(sqrt(5 / (len(queue[0][0][1]))))
        self.answerbox.halign = 'center'
        self.answerbox.valign = "center"
        self.label.halign = 'left'
        self.label.valign = "top"

    def read(self):
        self.readbool = True
        self.answerbox.text = ""
        self.anotherbox.remove_widget(self.btn)
        self.anotherbox.remove_widget(self.btn2)
        thread = threading.Thread(target = self.updatelabel)
        thread.daemon = True
        thread.start()
    def start(self):
        global numtopics
        if numtopics > 0:
            self.read()
        else:
            self.label.text = "there are no topics. add topics to play"
            self.anotherbox.remove_widget(self.btn3)
            self.anotherbox.remove_widget(self.btn2)
            self.anotherbox.remove_widget(self.btn)
    def on_event1(self, obj):
        print("Typical event from", obj)
    def on_property(self, obj, value):
        print("Typical property change from", obj, "to", value)

    def on_anything(self, *args, **kwargs):
        print('The flexible function has *args of', str(args),
            "and **kwargs of", str(kwargs))


class DemoApp(App):

    def build(self):
        self.title = "Brain Team!"
        y = ReadBox()
        y.start()
        return y


if __name__ == "__main__":
    DemoApp().run()