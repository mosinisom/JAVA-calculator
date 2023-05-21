package calculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Calculator {

    // Переменные экрана, памяти, операции и временной переменной
    private String _screen;
    private String _memory;
    private String _op;
    private String _temp;

    // Состояние калькулятора
    private CalcState _state;

    // Получить содержимое экрана
    public String getScreen() {
        return _screen;
    }

    // Конструктор калькулятора
    public Calculator() {
        _screen = "0";
        _memory = "";
        _op = "";
        _state = CalcState.Input1;
    }

    // Обработка нажатия кнопки
    public void Press(String key) {
        try {
            // Обработка состояния калькулятора
            switch (_state) {
                case Input1:
                    _state = ProcessInput1(key);
                    break;
                case Input2:
                    _state = ProcessInput2(key);
                    break;
                case Operation:
                    _state = ProcessOperation(key);
                    break;
                case Result:
                    _state = ProcessResult(key);
                    break;
                case Error:
                    _state = ProcessError(key);
                    break;
            }
        } catch (Exception e) {
            // Обработка ошибки
            _screen = "Error";
            _state = CalcState.Error;
        }
    }

    // Обработка ввода первого числа
    private CalcState ProcessInput1(String key) {
        switch (GetKeyKind(key)) {
            case Digit:
                _screen = AddDigit(_screen, key);
                return CalcState.Input1;
            case Dot:
                _screen = AddDot(_screen);
                return CalcState.Input1;
            case ChangeSign:
                _screen = ChangeSign(_screen);
                return CalcState.Input1;
            case Operation:
                _memory = _screen;
                _op = key;
                return CalcState.Operation;
            case Result:
                return CalcState.Input1;
            case Clear:
                Clear();
                return CalcState.Input1;
            case Back:
                _screen = Back(_screen);
                return CalcState.Input1;
            default:
                return CalcState.Error;
        }
    }

    // Обработка ввода второго числа
    private CalcState ProcessInput2(String key) throws Exception {
        switch (GetKeyKind(key)) {
            case Digit:
                _screen = AddDigit(_screen, key);
                return CalcState.Input2;
            case Dot:
                _screen = AddDot(_screen);
                return CalcState.Input2;
            case ChangeSign:
                _screen = ChangeSign(_screen);
                return CalcState.Input2;
            case Operation:
                _screen = Calculate(_memory, _screen, _op);
                _memory = _screen;
                _op = key;
                return CalcState.Operation;
            case Result:
                _temp = _memory;
                _memory = _screen;
                _screen = _temp;
                if ("-".equals(_op) || "/".equals(_op)) {
                    _screen = Calculate(_screen, _memory, _op);
                } else {
                    _screen = Calculate(_memory, _screen, _op);
                }
                return CalcState.Result;
            case Clear:
                Clear();
                return CalcState.Input1;
            case Back:
                _screen = Back(_screen);
                return CalcState.Input2;
            default:
                return CalcState.Error;
        }
    }

    // Обработка операции
    private CalcState ProcessOperation(String key) throws Exception {
        switch (GetKeyKind(key)) {
            case Digit:
                _screen = key;
                return CalcState.Input2;
            case Dot:
                _screen = "0.";
                return CalcState.Input2;
            case ChangeSign:
                return CalcState.Operation;
            case Operation:
                _op = key;
                return CalcState.Operation;
            case Result:
                _screen = Calculate(_memory, _screen, _op);
                return CalcState.Result;
            case Clear:
                Clear();
                return CalcState.Input1;
            case Back:
                return CalcState.Operation;
            default:
                return CalcState.Error;
        }
    }

    // Обработка результата
    private CalcState ProcessResult(String key) throws Exception {
        switch (GetKeyKind(key)) {
            case Digit:
                _screen = key;
                return CalcState.Input1;
            case Dot:
                _screen = "0.";
                return CalcState.Input1;
            case ChangeSign:
                _screen = ChangeSign(_screen);
                return CalcState.Result;
            case Operation:
                _memory = _screen;
                _op = key;
                return CalcState.Operation;
            case Result:
                if ("-".equals(_op) || "/".equals(_op)) {
                    _screen = Calculate(_screen, _memory, _op);
                } else {
                    _screen = Calculate(_memory, _screen, _op);
                }
                return CalcState.Result;
            case Clear:
                Clear();
                return CalcState.Input1;
            case Back:
                return CalcState.Result;
            default:
                return CalcState.Error;
        }
    }

    // Вычисление результата
    public String Calculate(String arg1, String arg2, String op) throws Exception {
        double x = Double.parseDouble(arg1);
        double y = Double.parseDouble(arg2);
        double res = 0;

        switch (op) {
            case "+":
                res = x + y;
                break;
            case "-":
                res = x - y;
                break;
            case "*":
                res = x * y;
                break;
            case "/":
                if (y == 0)
                    throw new Exception();
                res = x / y;
                break;
            default:
                throw new Exception();
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("0.##########", symbols);
        return decimalFormat.format(res);
    }

    // Обработка ошибки
    private CalcState ProcessError(String key) {
        switch (GetKeyKind(key)) {
            case Digit:
            case Dot:
            case ChangeSign:
            case Operation:
            case Result:
            case Back:
                return CalcState.Error;
            case Clear:
                Clear();
                return CalcState.Input1;
            default:
                return CalcState.Error;
        }
    }

    // Добавление цифры
    public String AddDigit(String num, String key) {
        if (num.equals("0")) {
            if (key.equals("0"))
                return "0";
            else
                return key;
        }
        return num + key;
    }

    // Добавление точки
    public String AddDot(String num) {
        if (!num.contains("."))
            return num + ".";
        return num;
    }

    // Смена знака
    public String ChangeSign(String num) {
        if (num.equals("0"))
            return "0";
        if (num.startsWith("-"))
            return num.substring(1);
        return "-" + num;
    }

    // Удаление последней цифры
    public String Back(String num) {
        String res = num.substring(0, num.length() - 1);
        if (num.length() == 1)
            return "0";
        else if (num.length() == 2 && num.startsWith("-"))
            return "0";
        else if (res.endsWith("."))
            return res.substring(0, res.length() - 1);
        else
            return res;
    }

    // Очистка калькулятора
    private void Clear() {
        _screen = "0";
        _memory = "";
        _op = "";
    }

    // Определение типа нажатой кнопки
    private CalcKey GetKeyKind(String key) {
        switch (key) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                return CalcKey.Digit;
            case ".":
                return CalcKey.Dot;
            case "-/+":
                return CalcKey.ChangeSign;
            case "-":
            case "+":
            case "*":
            case "/":
                return CalcKey.Operation;
            case "=":
                return CalcKey.Result;
            case "C":
                return CalcKey.Clear;
            case "B":
                return CalcKey.Back;
            default:
                return CalcKey.Undefined;
        }
    }
}

// Перечисление состояний калькулятора
enum CalcState {
    Input1,
    Operation,
    Input2,
    Result,
    Error
}

// Перечисление типов нажатых кнопок
enum CalcKey {
    Undefined,
    Digit,
    Dot,
    ChangeSign,
    Operation,
    Result,
    Clear,
    Back,
}