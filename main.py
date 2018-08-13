import kivy
kivy.require('1.0.5')

from kivy.uix.floatlayout import FloatLayout
from kivy.app import App
from kivy.properties import ObjectProperty, StringProperty


class Controller(FloatLayout):
    pass


class ControllerApp(App):

    def build(self):
        return Controller()


if __name__ == '__main__':
    ControllerApp().run()