from kivy.uix.boxlayout import BoxLayout
from kivy.app import App
from kivy.uix.button import Button
from functools import partial
from kivy.uix.textinput import TextInput
from kivy.uix.label import Label
from time import sleep
import threading
import brain
from math import sqrt
queue = brain.tieronequeue(2)
class DemoBox(BoxLayout):
    """
    This class demonstrates various techniques that can be used for binding to
    events. Although parts could me made more optimal, advanced Python concepts
    are avoided for the sake of readability and clarity.
    """
    def __init__(self, **kwargs):
        super(DemoBox, self).__init__(**kwargs)
        self.orientation = "horizontal"

        # We start with binding to a normal event. The only argument
        # passed to the callback is the object which we have bound to.
        self.label = Label(text="my man!", size_hint = (.8,1))
        self.add_widget(self.label)
        anotherbox = BoxLayout(size_hint = (0.2,1))
        anotherbox.orientation = "vertical"
        btn = Button(text="button")
        btn2 = Button(text = "button 2")
        btn.bind(on_press=self.on_event)
        anotherbox.add_widget(btn)
        anotherbox.add_widget(btn2)
        self.add_widget(anotherbox)

    def on_enter(self, obj):
        print(obj.text)
    def updatelabel(self):
        global queue
        text = brain.split_into_sentences(queue[0][0][0])
        x = 0
        self.label.text = queue[0][0][0]
        self.label.text_size = self.label.size
        print(self.label.height, self.label.width)
        self.label.font_size = 0.044 * sqrt(self.label.height * self.label.width)
        self.label.halign = 'left'
        self.label.valign = "top"
        self.label.text = ""
        while x < len(text):
            self.label.text = self.label.text + text[x] + " "
            x = x + 1
            print(x)
            sleep(1)
    def on_event(self, obj):
        thread = threading.Thread(target = self.updatelabel)
        thread.daemon = True
        thread.start()
    def on_event1(self, obj):
        print("Typical event from", obj)
    def on_property(self, obj, value):
        print("Typical property change from", obj, "to", value)

    def on_anything(self, *args, **kwargs):
        print('The flexible function has *args of', str(args),
            "and **kwargs of", str(kwargs))


class DemoApp(App):
    def build(self):
        y = DemoBox()
        return y

if __name__ == "__main__":
    DemoApp().run()